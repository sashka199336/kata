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

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = $_POST['title'];
    $author = $_POST['author'];
    $short_content = $_POST['short_content'];
    $full_content = $_POST['full_content'];

    $stmt = $pdo->prepare("UPDATE messages SET title = :title, author = :author, short_content = :short_content, full_content = :full_content WHERE id = :id");
    $stmt->execute([
        'id' => $id,
        'title' => $title,
        'author' => $author,
        'short_content' => $short_content,
        'full_content' => $full_content,
    ]);

    header("Location: view.php?id=$id");
    exit;
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Редактировать сообщение</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<h1>Редакт