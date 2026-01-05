package com.example.demo.service.impl;

import com.example.demo.constant.RedisConstant;
import com.example.demo.exception.ErrorConstant;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.lock.RedisLock;
import com.example.demo.model.Notification;
import com.example.demo.mq.producer.NotificationProducer;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.req.AddNotificationReq;
import com.example.demo.req.UpdateNotificationReq;
import com.example.demo.res.NotificationRes;
import com.example.demo.service.INotificationService;
import com.example.demo.utils.EntityToResUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final RedisLock redisLock;

    private final NotificationProducer notificationProducer;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   RedisLock redisLock,
                                   NotificationProducer notificationProducer,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.notificationRepository = notificationRepository;
        this.redisLock = redisLock;
        this.notificationProducer = notificationProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    @CachePut(value = "notificationCache", key = "#result.id")
    public NotificationRes add(AddNotificationReq req) {

        String lockKey = RedisConstant.LOCK_NOTIFICATION + RedisConstant.LOCK_ADD_SUFFIX;
        String lockValue = UUID.randomUUID().toString();

        boolean locked = redisLock.tryLock(lockKey, lockValue, 30);

        if (!locked){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not acquire lock " + lockKey);
        }
        NotificationRes res ;
        try {
            Notification entity = new Notification();

            BeanUtils.copyProperties(req, entity);
            Notification saved = notificationRepository.save(entity);

            res = EntityToResUtils.entityToRes(saved, NotificationRes.class);
            redisTemplate.opsForValue().set(
                    RedisConstant.NOTIFICATION_KEY + res.getId(), res
            );

            redisTemplate.opsForList().leftPush(
                    RedisConstant.RECENT_NOTIFICATION_LIST, res);
            redisTemplate.opsForList().trim(
                    RedisConstant.RECENT_NOTIFICATION_LIST, 0, 9
            );

            notificationProducer.sendNotification(saved);
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
        return res;
    }

    @Override
    @Cacheable(value = "notificationCache", key = "#id")
    public NotificationRes getById(Long id) {
        String key = RedisConstant.NOTIFICATION_KEY + id;
        NotificationRes cached = (NotificationRes) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        Notification entity = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND)
        );
        return EntityToResUtils.entityToRes(entity, NotificationRes.class);
    }

    @Override
    public List<Object> recent() {
        return redisTemplate.opsForList().range(
                RedisConstant.RECENT_NOTIFICATION_LIST, 0, 9);
    }

    @Override
    @Transactional
    @CachePut(value = "notificationCache", key = "#id")
    public NotificationRes update(Long id, UpdateNotificationReq req) {
        String lockKey = RedisConstant.LOCK_NOTIFICATION + RedisConstant.LOCK_UPDATE_SUFFIX + id;
        String lockValue = UUID.randomUUID().toString();

        boolean locked = redisLock.tryLock(lockKey, lockValue, 30);
        if (!locked){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not acquire lock " + lockKey);
        }
        NotificationRes res;
        try {
            Notification entity = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND)
            );
            entity.setContent(req.getContent());
            entity.setSubject(req.getSubject());
            Notification updated = notificationRepository.save(entity);
            res = EntityToResUtils.entityToRes(updated, NotificationRes.class);

            String key = RedisConstant.NOTIFICATION_KEY + updated.getId();
            redisTemplate.opsForValue().set(key, res, 1, TimeUnit.MINUTES);
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
        return res;
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationCache", key = "#id")
    public void delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND);
        }

        notificationRepository.deleteById(id);
        try {
            redisTemplate.delete(RedisConstant.NOTIFICATION_KEY + id);
        } catch (Exception e) {
            // do nothing
        }
    }
}
