<?php

    include('./config/db.php');

    if(isset($_POST['submit'])){

    if (isset($_GET['cart'])) {

        $cart = $_GET['cart'];

    } else {
        echo "Cart is empty";
    }

    
        $name = $_POST['name'];
    
        $stmt = $connection->prepare("SELECT fullname FROM members WHERE fullname = ?");
        $stmt->bind_param("s", $name);
        $stmt->execute();
        $result = $stmt->get_result();
    
        if ($result->num_rows > 0) {

            $row = $result->fetch_assoc();
        
            $memberName = $row['fullname'];
            
            $accessId = $connection->prepare("SELECT member_id FROM members WHERE fullname = ?");
            $accessId->bind_param("s", $memberName);
            $accessId->execute();
            $idResult = $accessId->get_result();
            if ($idRow = $idResult->fetch_assoc()) {
                $member_id = $idRow['member_id'];
            }
        
            foreach($cart as $cartItem){
                $query = "INSERT INTO borrowing (member_id, book_id, borrow_date, return_date, status ) VALUES ('$member_id', '$cartItem', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'borrowed')";
                mysqli_query($connection, $query);
            }

            echo '<script>window.location.href="index.php"</script>';
        }else{
            echo '<script>window.location.href="newMember.php" </script>';
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
        background-image:url('./images.jpg');
        background-repeat:no-repeat;
        background-size:cover;
        display: flex;
        height: 90vh;
        justify-content: center;
        align-items: center;
    }
    input{
        height: 30px;
        width: 250px;
        border: 1px solid #ddd;
        border-radius: 5px;
        padding: 5px;
        outline:none;
        text-align: center;
    }
    form{
        backdrop-filter: blur(5px);
        width:500px;
        height:300px;
        margin-top:50px;
        display: flex;
        flex-direction: column;
        align-items: center;
        row-gap: 15px;
        border:1px solid white;
        row-gap: 20px;
    }
    button{
        width: 100px;
        height: 30px;
        border: none;
        border-radius: 5px;
        background-color: #4CAF50;
        color: white;
        cursor: pointer;
    }
    a{
        text-decoration: none;
        color:black;
    }
</style>
<body>
    <div class="container">
        <form action="" method="post">
            <h2>Enter Your Name To Verify Your Identity</h2>
        <input type="text" name="name" id="name" placeholder="Name">
        <button name='submit'>Submit</button>
        <a href='newMember.php'> click here to Add New Member</a>
    </form>
    </div>
</body>

<script>
function sendCartToPHP() {
    let cart = sessionStorage.getItem("cart");
    document.getElementById("cartData").value = cart;
}
</script>

</html>