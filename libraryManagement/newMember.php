<?php

    include './config/db.php';

    if(isset($_POST['submit'])){
        $_name = $_POST['name'];
        $_email = $_POST['email'];
        $_number = $_POST['phone'];
        $_address = $_POST['address'];
        $_membership_date = $_POST['membership_date'];

        $query = "INSERT INTO `members`(`fullname`, `email`, `phno`, `address`, `membership_date`) VALUES ('$_name','$_email','$_number','$_address','$_membership_date')";

        if(mysqli_query($connection, $query)){
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
    .container{
        backdrop-filter: blur(5px);
        width:500px;
        height:500px;
        margin-top:50px;
        display: flex;
        flex-direction: column;
        align-items: center;
        row-gap: 20px;
        border:1px solid white;
        border-radius: 10px;
    }
    .container:hover{
        box-shadow: 0 4px 8px white;
        transition: 0.3s;
    }
    input , select{
        height: 30px;
        width: 250px;
        border: 1px solid #ddd;
        border-radius: 5px;
        padding: 5px;
        outline:none;
        text-align: center;
    }
    button{
        width: 100px;
        height: 30px;
        border: none;
        border-radius: 5px;
        background-color: #4CAF50;
        color: white;
    }
</style>    
<body>
    <form action="" method="post">
    <div class="container">
        <h2>Add New Member</h2>
        <input type="text" name="name" id="name" placeholder="Name">
        <input type="text" name="email" id="email" placeholder="Email">
        <input type='number' name='phone' id='phone' placeholder='Phone Number'>
        <input type='text' name='address' id='address' placeholder='Address'>
        <input type='date' name='membership_date' id='membership_date' placeholder='Membership Date'>
        <select>
            <option value='active'>Active</option>
            <option value='inactive'>Inactive</option>
        </select>
        <button name='submit' type='submit'>Add Member</button>
    </div>
</form>
</body>
</html>