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
    ['username' => 'admin', 'password' => password_hash('admin', PASSWORD_DEFAULT), 'role' => 'ROLE_ADMIN'],
    ['username' => 'seller', 'password' => password_hash('seller', PASSWORD_DEFAULT), 'role' => 'ROLE_SELLER'],
    ['username' => 'user', 'password' => password_hash('user', PASSWORD_DEFAULT), 'role' => 'ROLE_USER'],
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
