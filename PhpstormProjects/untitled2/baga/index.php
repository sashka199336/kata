<?php
require 'db.php'; // Подключение к базе данных

// Настройки для постраничного вывода
$limit = 5; // Количество сообщений на страницу
$page = $_GET['page'] ?? 1; // Текущая страница (по умолчанию 1)
$offset = ($page - 1) * $limit; // Смещение для SQL-запроса

// Получение сообщений из базы данных
$stmt = $pdo->prepare("SELECT * FROM messages ORDER BY created_at DESC LIMIT :limit OFFSET :offset");
$stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
$stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
$stmt->execute();
$messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Подсчет общего количества сообщений
$totalMessages = $pdo->query("SELECT COUNT(*) FROM messages")->fetchColumn();
$totalPages = ceil($totalMessages / $limit); // Количество страниц
?>

<!DOCTYPE html>
<html>
<head>
    <title>Список сообщений</title>
    <link rel="stylesheet" type="text/css" href="assets/style.css">
</head>
<body>
<div class="container">
    <h1>Список сообщений</h1>
    <a href="add_message.php" class="button">Добавить сообщение</a> <!-- Ссылка для добавления нового сообщения -->
    <ul>
        <?php foreach ($messages as $message): ?>
            <li>
                <h2>
                    <a href="message.php?id=<?= $message['id'] ?>">
                        <?= htmlspecialchars($message['title']) ?>
                    </a>
                </h2>
                <p><?= htmlspecialchars($message['short_content']) ?></p>
                <a href="edit_message.php?id=<?= $message['id'] ?>" class="button">Редактировать</a> <!-- Ссылка для редактирования -->
            </li>
        <?php endforeach; ?>
    </ul>

    <!-- навигация страниц -->
    <div class="pagination">
        <?php for ($i = 1; $i <= $totalPages; $i++): ?>
            <a href="?page=<?= $i ?>" class="<?= ($i == $page) ? 'active' : '' ?>">
                <?= $i ?>
            </a>
        <?php endfor; ?>
    </div>
</div>
</body>
</html>