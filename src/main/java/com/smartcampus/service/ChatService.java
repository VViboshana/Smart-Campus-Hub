package com.smartcampus.service;

import com.smartcampus.dto.response.ChatResponse;
import com.smartcampus.model.Resource;
import com.smartcampus.model.ResourceType;
import com.smartcampus.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ResourceRepository resourceRepository;

    public ChatResponse processMessage(String message) {
        String msg = message.toLowerCase().trim();

        // Greeting
        if (matchesAny(msg, "hello", "hi", "hey", "good morning", "good afternoon", "good evening", "howdy", "greetings")) {
            return ChatResponse.builder()
                    .reply("👋 Hello! Welcome to the Smart Campus Hub assistant. I can help you with booking resources, managing tickets, navigating the platform, and more. What would you like to know?")
                    .category("greeting")
                    .suggestions(List.of("How do I book a resource?", "What resources are available?", "How do I create a ticket?", "Tell me about this platform"))
                    .build();
        }

        // Farewell
        if (matchesAny(msg, "bye", "goodbye", "see you", "thanks bye", "thank you bye", "exit", "quit")) {
            return ChatResponse.builder()
                    .reply("👋 Goodbye! Feel free to come back anytime you need help. Have a great day!")
                    .category("farewell")
                    .suggestions(List.of())
                    .build();
        }

        // Thanks
        if (matchesAny(msg, "thanks", "thank you", "thx", "ty", "appreciate")) {
            return ChatResponse.builder()
                    .reply("😊 You're welcome! Is there anything else I can help you with?")
                    .category("thanks")
                    .suggestions(List.of("How do I book a resource?", "Tell me about tickets", "What can you do?"))
                    .build();
        }

        // About platform
        if (matchesAny(msg, "about", "what is this", "what is smart campus", "tell me about", "platform", "what does this do", "purpose")) {
            return ChatResponse.builder()
                    .reply("🏫 **Smart Campus Operations Hub** is a comprehensive campus management platform built for SLIIT. It allows you to:\n\n" +
                            "• **Browse & Book Resources** — Lecture halls, labs, projectors, study rooms\n" +
                            "• **Submit Support Tickets** — Report issues with campus facilities\n" +
                            "• **Track Notifications** — Stay updated on booking approvals and ticket status\n" +
                            "• **Admin Dashboard** — Manage resources, bookings, and users\n\n" +
                            "It's your one-stop hub for campus operations!")
                    .category("about")
                    .suggestions(List.of("How do I book a resource?", "How do I create a ticket?", "What resources are available?"))
                    .build();
        }

        // Resources — availability / listing
        if (matchesAny(msg, "resource", "available resource", "what resource", "list resource", "show resource", "facilities", "rooms", "labs", "halls", "equipment")) {
            List<Resource> resources = resourceRepository.findAll();
            if (resources.isEmpty()) {
                return ChatResponse.builder()
                        .reply("📋 There are currently no resources listed in the system. An admin can add resources from the Resources management page.")
                        .category("resources")
                        .suggestions(List.of("How do I book a resource?", "Who can add resources?"))
                        .build();
            }

            Map<ResourceType, List<Resource>> grouped = resources.stream()
                    .collect(Collectors.groupingBy(Resource::getType));

            StringBuilder sb = new StringBuilder("📋 **Available Campus Resources:**\n\n");
            for (Map.Entry<ResourceType, List<Resource>> entry : grouped.entrySet()) {
                sb.append("**").append(formatResourceType(entry.getKey())).append(":**\n");
                for (Resource r : entry.getValue()) {
                    sb.append("• ").append(r.getName());
                    if (r.getLocation() != null) sb.append(" — ").append(r.getLocation());
                    if (r.getCapacity() > 0) sb.append(" (Capacity: ").append(r.getCapacity()).append(")");
                    sb.append("\n");
                }
                sb.append("\n");
            }
            sb.append("You can view details and book any of these from the **Resources** page.");

            return ChatResponse.builder()
                    .reply(sb.toString())
                    .category("resources")
                    .suggestions(List.of("How do I book a resource?", "Show me lecture halls", "Show me labs"))
                    .build();
        }

        // Booking — how to
        if (matchesAny(msg, "book", "booking", "reserve", "reservation", "how to book", "make a booking", "schedule")) {
            return ChatResponse.builder()
                    .reply("📅 **How to Book a Resource:**\n\n" +
                            "1. Go to the **Resources** page from the navigation bar\n" +
                            "2. Browse or search for the resource you need\n" +
                            "3. Click on the resource to view its details\n" +
                            "4. Click the **Book Now** button\n" +
                            "5. Select your desired **date**, **start time**, and **end time**\n" +
                            "6. Add a **purpose** for the booking\n" +
                            "7. Submit your request\n\n" +
                            "Your booking will be **pending** until an admin approves it. You'll receive a notification once it's reviewed!")
                    .category("booking")
                    .suggestions(List.of("What resources are available?", "Where can I see my bookings?", "Can I cancel a booking?"))
                    .build();
        }

        // My bookings
        if (matchesAny(msg, "my booking", "view booking", "check booking", "booking status", "see my booking", "where are my booking")) {
            return ChatResponse.builder()
                    .reply("📋 To view your bookings:\n\n" +
                            "1. Click **Bookings** in the navigation bar\n" +
                            "2. You'll see all your bookings with their current status\n" +
                            "3. Status can be: **Pending** (awaiting approval), **Approved**, **Rejected**, or **Cancelled**\n\n" +
                            "You can also cancel a pending booking from this page if your plans change.")
                    .category("booking")
                    .suggestions(List.of("How do I book a resource?", "Can I cancel a booking?", "Who approves bookings?"))
                    .build();
        }

        // Cancel booking
        if (matchesAny(msg, "cancel booking", "cancel my booking", "cancel reservation", "cancel a booking")) {
            return ChatResponse.builder()
                    .reply("❌ **To cancel a booking:**\n\n" +
                            "1. Go to the **Bookings** page\n" +
                            "2. Find the booking you want to cancel\n" +
                            "3. Click the **Cancel** button (only available for pending bookings)\n\n" +
                            "Note: You can only cancel bookings that are still in **Pending** status.")
                    .category("booking")
                    .suggestions(List.of("How do I book a resource?", "Where can I see my bookings?"))
                    .build();
        }

        // Tickets — how to create
        if (matchesAny(msg, "ticket", "create ticket", "submit ticket", "report issue", "raise ticket", "support ticket", "maintenance", "issue", "problem", "complaint")) {
            return ChatResponse.builder()
                    .reply("🎫 **How to Create a Support Ticket:**\n\n" +
                            "1. Go to the **Tickets** page from the navigation bar\n" +
                            "2. Click **Create Ticket**\n" +
                            "3. Fill in:\n" +
                            "   • **Title** — Brief description of the issue\n" +
                            "   • **Description** — Detailed explanation\n" +
                            "   • **Category** — Select the relevant category\n" +
                            "   • **Priority** — Low, Medium, High, or Critical\n" +
                            "   • **Images** — Optionally attach photos\n" +
                            "4. Submit the ticket\n\n" +
                            "An admin or technician will review and respond to your ticket. You can track its progress and add comments!")
                    .category("ticket")
                    .suggestions(List.of("Where can I see my tickets?", "What are ticket priorities?", "Can I add comments?"))
                    .build();
        }

        // Ticket status / my tickets
        if (matchesAny(msg, "my ticket", "ticket status", "check ticket", "view ticket", "track ticket")) {
            return ChatResponse.builder()
                    .reply("📋 **To check your tickets:**\n\n" +
                            "1. Go to the **Tickets** page\n" +
                            "2. You'll see all your submitted tickets with their status\n" +
                            "3. Click on any ticket to view full details and comments\n\n" +
                            "**Ticket Statuses:**\n" +
                            "• 🟡 **Open** — Newly submitted\n" +
                            "• 🔵 **In Progress** — Being worked on\n" +
                            "• 🟢 **Resolved** — Issue fixed\n" +
                            "• ⚫ **Closed** — Ticket closed")
                    .category("ticket")
                    .suggestions(List.of("How do I create a ticket?", "Can I add comments to a ticket?"))
                    .build();
        }

        // Comments
        if (matchesAny(msg, "comment", "add comment", "reply to ticket", "respond to ticket")) {
            return ChatResponse.builder()
                    .reply("💬 **Adding Comments to Tickets:**\n\n" +
                            "1. Go to the **Tickets** page and click on a ticket\n" +
                            "2. Scroll down to the comments section\n" +
                            "3. Type your comment and click **Submit**\n\n" +
                            "Both users and admins/technicians can add comments to communicate about the issue.")
                    .category("comment")
                    .suggestions(List.of("How do I create a ticket?", "Can I edit my comment?"))
                    .build();
        }

        // Notifications
        if (matchesAny(msg, "notification", "alert", "unread", "bell", "notify")) {
            return ChatResponse.builder()
                    .reply("🔔 **Notifications:**\n\n" +
                            "You'll receive notifications for:\n" +
                            "• Booking approvals or rejections\n" +
                            "• Ticket status updates\n" +
                            "• New comments on your tickets\n\n" +
                            "Click the **bell icon** in the navbar to view your notifications. Unread notifications are shown with a badge count.")
                    .category("notification")
                    .suggestions(List.of("How do I mark notifications as read?", "Where are my bookings?"))
                    .build();
        }

        // Admin features
        if (matchesAny(msg, "admin", "manage", "admin panel", "admin feature", "manage user", "user management", "roles")) {
            return ChatResponse.builder()
                    .reply("👑 **Admin Features:**\n\n" +
                            "Admins have access to additional features:\n\n" +
                            "• **Manage Bookings** — Approve or reject booking requests\n" +
                            "• **Manage Tickets** — Review, assign technicians, update ticket status\n" +
                            "• **Manage Users** — View all users, update roles (Admin/User/Technician)\n" +
                            "• **Create Resources** — Add new campus resources (rooms, labs, equipment)\n\n" +
                            "Admin options are available in the dropdown menu in the navigation bar.")
                    .category("admin")
                    .suggestions(List.of("How do I create a resource?", "How do I approve bookings?", "How do I assign a technician?"))
                    .build();
        }

        // Create resource (admin)
        if (matchesAny(msg, "create resource", "add resource", "new resource", "register resource")) {
            return ChatResponse.builder()
                    .reply("➕ **Creating a New Resource (Admin only):**\n\n" +
                            "1. Go to the **Resources** page\n" +
                            "2. Click **Add Resource**\n" +
                            "3. Fill in:\n" +
                            "   • **Name** — Resource name\n" +
                            "   • **Type** — Lecture Hall, Lab, Study Room, Equipment, etc.\n" +
                            "   • **Location** — Where it's located\n" +
                            "   • **Capacity** — Number of people it can accommodate\n" +
                            "   • **Description** — Additional details\n" +
                            "4. Click **Create**\n\n" +
                            "Note: Only admins can create, edit, or delete resources.")
                    .category("admin")
                    .suggestions(List.of("What resources are available?", "How do I book a resource?"))
                    .build();
        }

        // Login / Authentication
        if (matchesAny(msg, "login", "sign in", "log in", "authentication", "password", "account", "register", "sign up", "google login", "oauth")) {
            return ChatResponse.builder()
                    .reply("🔐 **Authentication:**\n\n" +
                            "You can access the platform in two ways:\n\n" +
                            "1. **Email & Password** — Register with your email, then log in\n" +
                            "2. **Google Sign-In** — Click \"Sign in with Google\" for quick access\n\n" +
                            "After logging in, you'll be redirected to your Dashboard with an overview of your bookings, tickets, and notifications.")
                    .category("auth")
                    .suggestions(List.of("Tell me about the dashboard", "What can I do on this platform?"))
                    .build();
        }

        // Dashboard
        if (matchesAny(msg, "dashboard", "home", "overview", "main page")) {
            return ChatResponse.builder()
                    .reply("🏠 **Dashboard:**\n\n" +
                            "The Dashboard is your home page and shows:\n\n" +
                            "• **Resource count** — Total campus resources\n" +
                            "• **My Bookings** — Number of your bookings\n" +
                            "• **My Tickets** — Number of your tickets\n" +
                            "• **Unread Alerts** — Notification count\n" +
                            "• **Recent Bookings** — Your latest booking activity\n" +
                            "• **Recent Tickets** — Your latest ticket activity\n\n" +
                            "Click any card to navigate to that section.")
                    .category("dashboard")
                    .suggestions(List.of("How do I book a resource?", "How do I create a ticket?", "What resources are available?"))
                    .build();
        }

        // Help / what can you do
        if (matchesAny(msg, "help", "what can you do", "capabilities", "features", "options", "menu", "commands")) {
            return ChatResponse.builder()
                    .reply("🤖 **I can help you with:**\n\n" +
                            "📋 **Resources** — View available campus resources\n" +
                            "📅 **Bookings** — How to book, view, or cancel bookings\n" +
                            "🎫 **Tickets** — Create & track support tickets\n" +
                            "💬 **Comments** — Add comments to tickets\n" +
                            "🔔 **Notifications** — Manage your alerts\n" +
                            "👑 **Admin** — Admin features & management\n" +
                            "🔐 **Account** — Login & authentication help\n" +
                            "🏠 **Dashboard** — Platform overview\n\n" +
                            "Just type your question or pick a suggestion below!")
                    .category("help")
                    .suggestions(List.of("What resources are available?", "How do I book a resource?", "How do I create a ticket?", "Tell me about this platform"))
                    .build();
        }

        // Specific resource type queries
        if (matchesAny(msg, "lecture hall", "auditorium")) {
            return buildResourceTypeResponse(ResourceType.LECTURE_HALL, "Lecture Halls & Auditoriums");
        }
        if (matchesAny(msg, "lab", "laboratory")) {
            return buildResourceTypeResponse(ResourceType.LAB, "Labs & Laboratories");
        }
        if (matchesAny(msg, "meeting room", "seminar")) {
            return buildResourceTypeResponse(ResourceType.MEETING_ROOM, "Meeting Rooms");
        }
        if (matchesAny(msg, "projector")) {
            return buildResourceTypeResponse(ResourceType.PROJECTOR, "Projectors");
        }
        if (matchesAny(msg, "camera")) {
            return buildResourceTypeResponse(ResourceType.CAMERA, "Cameras");
        }
        if (matchesAny(msg, "equipment", "printer")) {
            return buildResourceTypeResponse(ResourceType.EQUIPMENT, "Equipment");
        }

        // Default — unrecognized
        return ChatResponse.builder()
                .reply("🤔 I'm not sure I understand that. I'm the Smart Campus Hub assistant and I can help with resources, bookings, tickets, and more. Could you try rephrasing, or pick one of the suggestions below?")
                .category("unknown")
                .suggestions(List.of("What can you do?", "How do I book a resource?", "How do I create a ticket?", "What resources are available?"))
                .build();
    }

    private ChatResponse buildResourceTypeResponse(ResourceType type, String label) {
        List<Resource> resources = resourceRepository.findByType(type);
        if (resources.isEmpty()) {
            return ChatResponse.builder()
                    .reply("📋 No **" + label + "** are currently listed. An admin can add them from the Resources page.")
                    .category("resources")
                    .suggestions(List.of("What resources are available?", "How do I book a resource?"))
                    .build();
        }

        StringBuilder sb = new StringBuilder("📋 **" + label + ":**\n\n");
        for (Resource r : resources) {
            sb.append("• **").append(r.getName()).append("**");
            if (r.getLocation() != null) sb.append(" — ").append(r.getLocation());
            if (r.getCapacity() > 0) sb.append(" (Capacity: ").append(r.getCapacity()).append(")");
            sb.append(" [").append(r.getStatus()).append("]\n");
        }
        sb.append("\nGo to the **Resources** page to view details or book one.");

        return ChatResponse.builder()
                .reply(sb.toString())
                .category("resources")
                .suggestions(List.of("How do I book a resource?", "Show all resources", "Tell me about bookings"))
                .build();
    }

    private boolean matchesAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) return true;
        }
        return false;
    }

    private String formatResourceType(ResourceType type) {
        return switch (type) {
            case LECTURE_HALL -> "Lecture Halls";
            case LAB -> "Labs";
            case MEETING_ROOM -> "Meeting Rooms";
            case PROJECTOR -> "Projectors";
            case CAMERA -> "Cameras";
            case EQUIPMENT -> "Equipment";
        };
    }
}
