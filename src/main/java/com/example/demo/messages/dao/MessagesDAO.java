package com.example.demo.messages.dao;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;

import java.util.List;

public interface MessagesDAO {
    List<Persons> getRecentChatUsers(String currentUserId, int limit); // Danh sách người đã nhắn (20 gần nhất)
    List<Messages> getMessagesBetweenUsers(String user1Id, String user2Id, int page, int size); // Lịch sử tin nhắn 1-1
    Messages save(Messages message); // Gửi tin nhắn mới
    Long countUnreadMessages(String recipientId); // Đếm tin nhắn chưa đọc
    void markAsRead(String currentUserId, String senderId); // Đánh dấu đã đọc
    Messages getLatestMessage(String user1Id, String user2Id); // Tin nhắn mới nhất để sắp xếp danh sách
}