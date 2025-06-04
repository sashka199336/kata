<?php
require 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = $_POST['title'];
    $author = $_POST['author'];
    $shortContent = $_POST['short_content'];
    $fullContent = $_POST['full_content'];

    $stmt = $pdo->prepare("INSERT INTO messages (title, author, short_content, full_content) VALUES (:title, :author, :short_content, :full_content)");
    $stmt->execute([
        ':title' => $title,
        ':author' => $author,
        ':short_content' => $shortContent,
        ':full_content' => $fullContent
    ]);

    header('Location: index.php');
    exit;
}
?>
<!DOCTYPE html>
<html>
<head>
    <title>Добавить сообщение</title>
</head>
<body>
<h1>Добавить сообщение</h1>
<form action="" method="post">
    <input type="text" name="title" placeholder="Заголовок" required>
    <input type="text" name="author" placeholder="Автор" required>
    <textarea name="short_content" placeholder="Краткое содержание" required></textarea>
    <textarea name="full_content" placeholder="Полное содержание" required></textarea>
    <button type="submit">Сохранить</button>
</form>
</body>
</html>