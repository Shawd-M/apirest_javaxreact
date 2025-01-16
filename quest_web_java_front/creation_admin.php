#!/usr/bin/env php
<?php

$servername = "localhost";
$username = "josephD";
$password = "admin";
$dbname = "quest_web";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$users = [
    ['username' => 'admin_user', 'password' => password_hash('admin123', PASSWORD_DEFAULT), 'role' => 'admin'],
    ['username' => 'seller_user', 'password' => password_hash('seller123', PASSWORD_DEFAULT), 'role' => 'seller'],
    ['username' => 'regular_user', 'password' => password_hash('user123', PASSWORD_DEFAULT), 'role' => 'user'],
];

foreach ($users as $user) {
    $sql = "INSERT INTO user (username, password, role, creation_date, updated_date) 
            VALUES ('{$user['username']}', '{$user['password']}', '{$user['role']}', NOW(), NOW())";
    
    if ($conn->query($sql) === TRUE) {
        echo "User {$user['username']} created successfully.\n";
    } else {
        echo "Error creating user {$user['username']}: " . $conn->error . "\n";
    }
}

$conn->close();
?>
