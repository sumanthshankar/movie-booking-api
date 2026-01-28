package movie.cache;

import lombok.extern.slf4j.Slf4j;
import movie.exception.LockException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DistributedLockService {
    private final RedissonClient redissonClient;

    @Autowired
    public DistributedLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void acquireLock(Long showId, Long seatId) {
        String lockKey = "LOCK:SHOW:" + showId + ":SEAT:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new LockException("Seat is currently being booked by another user.");
            }
        } catch (InterruptedException e) {
            throw new LockException("Interrupted while acquiring lock.");
        }
    }

    public void releaseLock(Long showId, Long seatId) {
        String lockKey = "LOCK:SHOW:" + showId + ":SEAT:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
