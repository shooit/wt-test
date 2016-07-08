#WillowTree Test Project

##Taxonomy and Product API

###Taxonomies
A taxonomy is a level in the product organization tree for a retailer

Each taxonomy has an id and a name, as well as an optional parent and children

The web service launches at [localhost:8080/wt-test/taxonomies]

####API

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
        
###Products
Products fall under a certain taxonomy