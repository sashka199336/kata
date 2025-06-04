<?php
require 'db.php';

$id = $_GET['id'] ?? null;
if (!$id) {
    die("Сообщение не найдено!");
}

// Получение сообщения
$stmt = $pdo->prepare("SELECT * FROM messages WHERE id = :id");
$stmt->execute(['id' => $id]);
$message = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$message) {
    die("Сообщение не найдено!");
}

// Получение комментариев
$commentsStmt = $pdo->prepare("SELECT * FROM comments WHERE message_id = :id ORDER BY created_at DESC");
$commentsStmt->execute(['id' => $id]);
$comments = $commentsStmt->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><?= htmlspecialchars($message['title']) ?></title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<h1><?= htmlspecialchars($message['title']) ?></h1>
<p><strong>Автор:</strong> <?= htmlspecialchars($message['author']) ?></p>
<p><?= nl2br(htmlspecialchars($message['full_content'])) ?></p>
<a href="edit.php?id=<?= $message['id'] ?>">Редактировать</a>

<h2>Комментарии</h2>
<ul>
    <?php foreach ($comments as $comment): ?>
        <li>
            <strong><?= htmlspecialchars($comment['author']) ?></strong> <?= htmlspecialchars($comment['content']) ?>
        </li>
    <?php endforeach; ?>
</ul>

<h3>Добавить комментарий</h3>
<form action="comment.php" method="POST">
    <input type="hidden" name="message_id" value="<?= $message['id'] ?>">
    <label for="author">Автор:</label>
    <input type="text" name="author" id="author" required>
    <label for="content">Комментарий:</label>
    <textarea name="content" id="content" required></textarea>
    <button type="submit">Добавить</button>
</form>
</body>
</html>