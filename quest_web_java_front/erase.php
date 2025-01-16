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

$conn->query("SET FOREIGN_KEY_CHECKS = 0");

$tables = ['address', 'order_product', 'orders', 'product', 'user'];

foreach ($tables as $table) {
    $sql = "TRUNCATE TABLE $table";
    if ($conn->query($sql) === TRUE) {
        echo "Table $table truncated successfully.\n";
    } else {
        echo "Error truncating table $table: " . $conn->error . "\n";
    }
}

$conn->query("SET FOREIGN_KEY_CHECKS = 1");

$conn->close();
?>
