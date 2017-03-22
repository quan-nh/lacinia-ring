# try-lacinia

Playing with [walmartlabs/lacinia](https://github.com/walmartlabs/lacinia) library, a GraphQL implementation in pure Clojure.

See [blog post](https://kipalog.com/posts/try-lacinia) for detail.

## Usage

Start web service at port 3000
```sh
lein ring server-headless
```

Then, send POST requests to `/graphql` url with query data
```sh
http POST http://localhost:3000/graphql \
	 Content-Type:application/graphql \
	 <<< '{
            hero {
              id
              name
              friends {
                name
              }
            }
          }'
-->
{
    "data": {
        "hero": {
            "friends": [
                {
                    "name": "Luke Skywalker"
                }, 
                {
                    "name": "Han Solo"
                }, 
                {
                    "name": "Leia Organa"
                }
            ], 
            "id": "2001", 
            "name": "R2-D2"
        }
    }
}


http POST http://localhost:3000/graphql \
	 Content-Type:application/graphql \
	 <<< '{
            human(id: "1001") {
              name
            }
          }'
-->
{
    "data": {
        "human": {
            "name": "Darth Vader"
        }
    }
}
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
