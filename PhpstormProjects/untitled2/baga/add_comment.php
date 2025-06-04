<?php
require 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $messageId = $_POST['message_id'];
    $author = $_POST['author'];
    $content = $_POST['content'];

    $stmt = $pdo->prepare("INSERT INTO comments (message_id, author, content) VALUES (:message_id, :author, :content)");
    $stmt->execute([
        ':message_id' => $messageId,
        ':author' => $author,
        ':content' => $content
    ]);

    header("Location: message.php?id=$messageId");
    exit;
}
?>