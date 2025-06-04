<?php
require 'db.php';

$messageId = $_GET['id'] ?? null;

if (!$messageId) {
    die("Сообщение не найдено");
}

// Получаем сообщение
$stmt = $pdo->prepare("SELECT * FROM messages WHERE id = :id");
$stmt->execute([':id' => $messageId]);
$message = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$message) {
    die("Сообщение не найдено");
}

// Получаем комментарии
$stmt = $pdo->prepare("SELECT * FROM comments WHERE message_id = :message_id ORDER BY created_at ASC");
$stmt->execute([':message_id' => $messageId]);
$comments = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<!DOCTYPE html>
<html>
<head>
    <title><?= htmlspecialchars($message['title']) ?></title>
</head>
<body>
<h1><?= htmlspecialchars($message['title']) ?></h1>
<p><strong>Автор:</strong> <?= htmlspecialchars($message['author']) ?></p>
<p><?= htmlspecialchars($message['full_content']) ?></p>

<h2>Комментарии</h2>
<ul>
    <?php foreach ($comments as $comment): ?>
        <li>
            <p><strong><?= htmlspecialchars($comment['author']) ?>:</strong> <?= htmlspecialchars($comment['content']) ?></p>
        </li>
    <?php endforeach; ?>
</ul>

<h3>Добавить комментарий</h3>
<form action="add_comment.php" method="post">
    <input type="hidden" name="message_id" value="<?= $message['id'] ?>">
    <input type="text" name="author" placeholder="Ваше имя" required>
    <textarea name="content" placeholder="Комментарий" required></textarea>
    <button type="submit">Добавить</button>
</form>
</body>
</html>