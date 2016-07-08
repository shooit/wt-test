#WillowTree Test Project

##Project Architecture

This project is written in Scala with a SQL back end.  ScalikeJDBC is used to interface with the database. Scalatra is used to power the web service.

###ER Diagram
[ER diagram](link)

###Taxonomy and Product API

####Taxonomies
A taxonomy is a level in the product organization tree for a retailer

Each taxonomy has an id and a name, as well as an optional parent and children

The web service launches at [localhost:8080/wt-test/taxonomies]

#####API

1. **GET /** will return a list of all taxonomies
    Params: 
        1. (optional) name: filter the taxonomies by name
    Examples: 
        1. [localhost:8080/wt-test/taxonomies]
        2. [localhost:8080/wt-test/taxonomies?name=Home%20Improvement]
    
        
2. **GET /id** will return a specific taxonomy by id
    Params:
        1. (optional) tree: return the taxonomies in tree form with root = id
    Examples:
        1. [localhost:8080/wt-test/taxonomies/1]
        2. [localhost:8080/wt-test/taxonomies/1?tree]

3. **POST /** accepts a list of json taxonomy objects to be inserted
    Headers: "Content-Type: application/json"
    Schema \[ { "id": "2", "name": "I'm a taxonomy", "parent": "1" , "children": [] }, ... ]

4. **PUT /id** updates the taxonomy with the given id to a new parent
    Params:
        1. parent: the id of the new parent
    Example:
        1. [localhost:8080/wt-test/taxonomies/11?parent=10
        2. [localhost:8080/wt-test/taxonomies/11?parent=6
        
5. **DELETE /id** removes the taxonomy with the given id
    Example:
        1. [localhost:8080/wt-test/taxonomies/11]
        
####Products
Each product has an id, name, price, category (taxonomy) and an optional description

The web service launches at [localhost:8080/wt-test/products]

#####API
1. **GET /** will return a list of all products
    Params: 
        1. (optional) category: filter the products by category
        2. (optional) sub: returns the products in the category and all of its sub categories
    Examples: 
        1. [localhost:8080/wt-test/products]
        2. [localhost:8080/wt-test/products?category=6]
        3. [localhost:8080/wt-test/products?category=1&sub]
   
2. **GET /id** will return a specific product by id
    Example:
        1. [localhost:8080/wt-test/product/1]

3. **POST /** accepts a list of json product objects to be inserted
    Headers: "Content-Type: application/json"
    Schema \[ { "id": "2", "name": "road runner catcher", "brand": "ACME" , "description": "works great!", "category": "5", "price": 19.99 }, ... ]

4. **PUT /id** updates the product with the given id to a new category
    Params:
        1. category: the new category
    Example:
        1. [localhost:8080/wt-test/products/1?category=2
        2. [localhost:8080/wt-test/product/1?category=9
        
5. **DELETE /id** removes the taxonomy with the given id
    Example:
        1. [localhost:8080/wt-test/products/1]
        
##Next Steps
1. Better error/message pass back to the requester
2. Handle taxonomy insertion more safely:
ScalikeJDBC and SQLITE were not playing nice to each other and I could not get a INSERT OR IGNORE flag to work. 
You can currently only pass string references to other taxonomies which forces the user to insert parents before children rather than being able to pass something that looks like a tree
3. Fuzzy matching on names and descriptions: 
Transitioning free text to a document data store like ElasticSearch would allow for more complicated search ability out of the box
This would require a certain amount of federated search/insert because relationships would likely continue to be maintained in a relational table
        