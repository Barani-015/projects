<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
</head>
<style>
    body{
        background-image:url('./images.jpg');
        background-repeat:no-repeat;
        background-size:cover;
        padding: 40px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        background-color: rgba(255, 255, 255, 0.8);
    }
    th, td {
        border: 1px solid #ddd;
        padding: 8px;
        text-align: center;
        font-weight: bold;
    }
    th {
        background-color: #f2f2f2;
    }
    h1{
        color: white;
        text-shadow: 2px 2px 4px black;
    }
</style>
<body>
    <h1>Welcome to the Library Management System</h1>
    <br />

    <a href='addBook.php' class='btn btn-success'>Add Book</a><br>
    <div class='container'>
    <table>
        <thead>

            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Author</th>
                <th>Published Date</th>
                <th>Available Copies</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <?php
                include './config/db.php';

                $query = "SELECT * FROM books";

                $result = mysqli_query($connection, $query);

                while($row = mysqli_fetch_assoc($result)){
                    echo "<tr>
                            <td>{$row['book_id']}</td>
                            <td>{$row['title']}</td>
                            <td>{$row['author']}</td>
                            <td>{$row['publication_year']}</td>
                            <td>{$row['available_copies']}</td>
                            <td><button class='btn btn-info' onclick='clicker(this, " . $row['book_id'] . ")'><p id='button-value'>Borrow</p></button></td>
                        </tr>";
                }
            ?>

        </tbody>
    </table>
            </div><br />
    <button class="btn btn-primary" onclick="goToCart()">Go to Cart</button>
    
</body>

<script>
        let cart = [];
        let query = cart.map(id => "cart[]=" + id).join("&");

    function clicker(button, id){
        cart.push(id);
        sessionStorage.setItem("cart", JSON.stringify(cart));
        console.log(cart);

        let buttonValue = button.querySelector("#button-value");
        buttonValue.textContent = "Added";
        button.disabled = true;
    }
    function goToCart() {
        let cart = JSON.parse(sessionStorage.getItem("cart")) || [];
        let query = cart.map(id => "cart[]=" + id).join("&");
        window.location.href = "verify.php?" + query;
    }

    

</script>
</html>