<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
   <head>
      <title>DC UFSCar</title>
      <style>.error { color: red; }</style>
   </head>
   <body>
      <form method="POST" enctype="multipart/form-data" action="index">
         <h1>Choose your model file:</h1>
         <p>
            <label for="upFile">File</label>
            <input type="file" name="upFile">
         </p>

         <p>
            <input type="submit">
         </p>
            <span class="error">
               <c:forEach var="entry" items="${messages}">
                  ${entry.key}: ${entry.value}
                  <br>
               </c:forEach>
            </span>
         </p>
      </form>
   </body>
</html>
