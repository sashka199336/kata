<?php
require 'db.php'; // Подключение к базе данных

// Проверяем, передан ли ID сообщения через GET
$message_id = $_GET['id'] ?? null;

if (!$message_id) {
    die('Не указан ID сообщения для редактирования.');
}

// Если запрос GET - получаем данные сообщения из базы
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    // Получаем данные сообщения из базы
    $stmt = $pdo->prepare('SELECT * FROM messages WHERE id = :id');
    $stmt->execute([':id' => $message_id]);
    $message = $stmt->fetch(PDO::FETCH_ASSOC);

    // Если сообщение не найдено
    if (!$message) {
        die('Сообщение не найдено.');
    }
}

// Если запрос POST - обновляем данные сообщения
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Получаем данные из формы
    $message_id = $_POST['message_id'] ?? null;
    $message_1 = $_POST['message_1'] ?? '';
    $message_2 = $_POST['message_2'] ?? '';
    $message_3 = $_POST['message_3'] ?? '';
    $message_4 = $_POST['message_4'] ?? '';

    if ($message_id) {
        // Обновляем данные в базе
        $stmt = $pdo->prepare(
            'UPDATE messages SET 
                title = :message_1, 
                short_content = :message_2, 
                full_content = :message_3, 
                author = :message_4 
            WHERE id = :id'
        );

        $stmt->execute([
            ':message_1' => $message_1,
            ':message_2' => $message_2,
            ':message_3' => $message_3,
            ':message_4' => $message_4,
            ':id' => $message_id
        ]);

        // Редирект на главную страницу после успешного обновления
        header('Location: index.php');
        exit;
    } else {
        echo 'Не указан ID сообщения.';
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактировать сообщение</title>
    <link rel="stylesheet" href="assets/style.css"> <!-- Подключение файла стилей -->
</head>
<body>
<div class="container">
    <h1>Редактировать сообщение</h1>
    <form action="" method="post">
        <!-- Передаем ID сообщения через скрытое поле -->
        <input type="hidden" name="message_id" value="<?= htmlspecialchars($message['id']) ?>">

        <!-- Поле для редактирования заголовка -->
        <textarea name="message_1" rows="3" cols="50" required><?= htmlspecialchars($message['title'] ?? '') ?></textarea><br>

        <!-- поле редактированиЯ краткого содержания -->
        <textarea name="message_2" rows="3" cols="50" required><?= htmlspecialchars($message['short_content'] ?? '') ?></textarea><br>

        <!-- Поле для редактирования полного содержания -->
        <textarea name="message_3" rows="3" cols="50" required><?= htmlspecialchars($message['full_content'] ?? '') ?></textarea><br>

        <!-- Поле для редактирования автора -->
        <textarea name="message_4" rows="3" cols="50" required><?= htmlspecialchars($message['author'] ?? '') ?></textarea><br>

        <!-- Кнопка для сохранения изменений -->
        <button type="submit">Сохранить изменения</button>
    </form>
</div>
</body>
</html>