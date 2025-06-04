<?php
require 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = $_POST['title'];
    $author = $_POST['author'];
    $short_content = $_POST['short_content'];
    $full_content = $_POST['full_content'];

    $stmt = $pdo->prepare("INSERT INTO messages (title, author, short_content, full_content) VALUES (:title, :author, :short_content, :full_content)");
    $stmt->execute([
        'title' => $title,
        'author' => $author,
        'short_content' => $short_content,
        'full_content' => $full_content,
    ]);

    header("Location: index.php");
    exit;
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Добавить сообщение</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<h1>Добавить сообщение</h1>
<form method="POST">
    <label for="title">Заголовок:</label>
    <input type="text" name="title" id="title" required>
    <label for="author">Автор:</label>
    <input type="text" name="author" id="author" required>
    <label for="short_content">Краткое содержание:</label>
    <textarea name="short_content" id="short_content" required></textarea>
    <label for="full_content">Полное содержание:</label>
    <textarea name="full_content" id="full_content" required></textarea>
    <button type="submit">Добавить</button>
</form>
</body>
</html>