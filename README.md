# C2C PoC Code
Pair of client/server apps to demonstrate C2C networking in Pivotal Cloud Foundry

## Requirements
* PCF with container networking enabled (1.12 or later recommended)
* Spring cloud services with service registry
* Maven intalled (e.g. `brew install maven`)

## Information
This is a pair of apps, a client and server that use Eureka's [direct registration method](http://docs.pivotal.io/spring-cloud-services/1-4/common/service-registry/writing-client-applications.html#register-using-c2c) to enable container-to-container networking.

The server simply prints out whatever the client sends it, or an error if it cannot connect (e.g. the network is blocked).

## Building
Run the provided script `build.sh`.

## Set up
Create a c2c service registry:
```
$ cf create-service p-service-registry standard c2c-registry
```

This may take some time to come up. On PWS I had to wait about 2-3 minutes.

Next push the client and server apps:
```
$ pushd server && cf push && popd && pushd client && cf push && popd
```

Verify the client app is working as intended by visiting it, for example:
```
$ curl http://c2c-client-random.cfapps.io
```

It should print a bit of information about itself:
```
Remote hello from c2c-client (IP: 10.240.155.75:8080)
```

Now check the server:
```
$ curl http://c2c-server-random.cfapps.io
```

It should return an error like this:
```
Could not reach client (networking problem?)
```

Enable C2C networking between the apps:
```
cf add-network-policy c2c-server --destination-app c2c-client
```

It may take a couple of seconds to work, but repeating the server request should now show it's connected to the client directly:
```
Remote hello from c2c-client (IP: 10.240.155.75:8080)
```

You can then disable the policy again if you like:
```
cf remove-network-policy c2c-server --destination-app c2c-client --port 8080 --protocol tcp
```

