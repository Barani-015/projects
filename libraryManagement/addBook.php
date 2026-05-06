<?php

    include './config/db.php';

   if(isset($_POST['submit'])){
        $title = $_POST['title'];
        $author = $_POST['author'];
        $isbn = $_POST['isbn'];
        $published_date = $_POST['published_date'];
        $total_copies = $_POST['total_copies'];
        $available_copies = $_POST['available_copies'];

        if(mysqli_query($connection,"INSERT INTO `books`(`title`, `author`,  `publication_year`, `total_copies`, `available_copies`) VALUES ('$title','$author','$published_date','$total_copies','$available_copies')")){
            echo '<script>window.location.href="index.php"</script>';
        }else{
            echo "<script> alert('Data Not Inserted'); </script>";
        }
   }

?>




<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<style>
    body{
        padding: 40px;
        display: flex;
        background-image:url('./images.jpg');
        background-repeat:no-repeat;
        background-size:cover;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: white;
    }
    input{
        height: 30px;
        width: 250px;
        border: 1px solid #ddd;
        border-radius: 5px;
        padding: 5px;
        outline:none;
    }
    form{
        backdrop-filter: blur(5px);
        width:500px;
        height:500px;
        margin-top:50px;
        display: flex;
        flex-direction: column;
        align-items: center;
        row-gap: 1px;
        font-size:16px;
        font-family: Arial, sans-serif;
        border: 1px solid #ddd;
        border-radius: 10px;
    }
    form:hover{
        box-shadow: 0 8px 16px white;
        transform: scale(1.02);
        transition:0.5s;
    }
    button{
        width: 100px;
        height: 40px;
        background-color: #4CAF50;
        color: white;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        margin-bottom: 20px;
    }
</style>
<body>
    <form action="addBook.php" method="post">
        <h2>Add New Book</h2>
        <label for="title">Title</label>
        <input type="text" id="title" name="title"><br>
        
        <label for="author">Author</label>
        <input type="text" id="author" name="author"><br>
        
        <label for="published_date">Published Date</label>
        <input type="date" id="published_date" name="published_date"><br>

        <label for='total_copies'>Total Copies</label>
        <input type="number" id="total_copies" name="total_copies"><br>

        <label for='available_copies'>Available Copies</label>
        <input type='number' id='available_copies' name='available_copies'><br>

        <button type="submit" name='submit'>Add Book</button>
    </form>
</body>
</html>