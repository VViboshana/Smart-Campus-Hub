package com.smartcampus.service;

import com.smartcampus.dto.request.CommentRequest;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.model.Comment;
import com.smartcampus.model.Notification;
import com.smartcampus.model.Ticket;
import com.smartcampus.model.User;
import com.smartcampus.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketService ticketService;
    private final NotificationService notificationService;

    public Comment addComment(String ticketId, CommentRequest request, User currentUser) {
        Ticket ticket = ticketService.getTicketById(ticketId);

        Comment comment = Comment.builder()
                .ticketId(ticketId)
                .authorId(currentUser.getId())
                .authorName(currentUser.getName())
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);

        // Notify ticket reporter if comment is by someone else
        if (!ticket.getReporterId().equals(currentUser.getId())) {
            notificationService.createNotification(
                    ticket.getReporterId(),
                    "New Comment on Ticket",
                    currentUser.getName() + " commented on your ticket: " + ticket.getTitle(),
                    Notification.NotificationType.TICKET_COMMENT,
                    ticket.getId()
            );
        }

        // Notify assigned technician if comment is by someone else
        if (ticket.getAssignedTechnicianId() != null
                && !ticket.getAssignedTechnicianId().equals(currentUser.getId())) {
            notificationService.createNotification(
                    ticket.getAssignedTechnicianId(),
                    "New Comment on Ticket",
                    currentUser.getName() + " commented on ticket: " + ticket.getTitle(),
                    Notification.NotificationType.TICKET_COMMENT,
                    ticket.getId()
            );
        }

        return comment;
    }

    public Comment updateComment(String commentId, CommentRequest request, User currentUser) {
        Comment comment = getCommentById(commentId);
        if (!comment.getAuthorId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only edit your own comments");
        }
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(String commentId, User currentUser) {
        Comment comment = getCommentById(commentId);
        boolean isOwner = comment.getAuthorId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }

    public List<Comment> getTicketComments(String ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public Comment getCommentById(String id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }
}
