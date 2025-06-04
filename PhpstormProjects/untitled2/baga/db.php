<?php
$host = 'localhost';
$dbname = 'blog_app'; // название базы данныхшщ
$user = 'root'; // Имя пользователя
$password = ''; // Пароль

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Ошибка подключения к базе данных: " . $e->getMessage());
}
?>