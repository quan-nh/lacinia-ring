# try-lacinia

Experiment [walmartlabs/lacinia](https://github.com/walmartlabs/lacinia) library, a GraphQL implementation in pure Clojure.

See [blog post](https://kipalog.com/posts/try-lacinia).

## Usage

Start web service at port 3000
```sh
lein ring server-headless
```

Then, send POST request to `/graphql` url with query data
```sh
http POST http://localhost:3000/graphql \
	 Content-Type:application/graphql \
	 <<< '{ hero(episode: NEWHOPE) { movies: appears_in } }'

HTTP/1.1 200 OK
Content-Length: 56
Content-Type: application/json; charset=utf-8
Date: Tue, 21 Mar 2017 14:34:53 GMT
Server: Jetty(9.2.10.v20150310)

{
    "data": {
        "hero": {
            "movies": [
                "NEWHOPE", 
                "EMPIRE", 
                "JEDI"
            ]
        }
    }
}
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
