package com.smartcampus.service;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.model.Notification;
import com.smartcampus.model.User;
import com.smartcampus.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @SuppressWarnings("null")
    public Notification createNotification(@NonNull String userId, String title, String message,
                                            Notification.NotificationType type, String referenceId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .build();
            return Objects.requireNonNull(notificationRepository.save(notification));
    }

    public List<Notification> getUserNotifications(@NonNull String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(@NonNull String userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(@NonNull String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @SuppressWarnings("null")
    public Notification markAsRead(@NonNull String notificationId, @NonNull User currentUser) {
        Notification notification = getOwnedNotification(notificationId, currentUser);
        notification.setRead(true);
        return Objects.requireNonNull(notificationRepository.save(notification));
    }

    public void markAllAsRead(@NonNull String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        if (!unread.isEmpty()) {
            notificationRepository.saveAll(unread);
        }
    }

    public void deleteNotification(@NonNull String notificationId, @NonNull User currentUser) {
        getOwnedNotification(notificationId, currentUser);
        notificationRepository.deleteById(notificationId);
    }

    private Notification getOwnedNotification(@NonNull String notificationId, @NonNull User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only access your own notifications");
        }

        return notification;
    }
}
