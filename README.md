# C2C PoC Code
Pair of backend/frontend apps to demonstrate C2C networking in Pivotal Cloud Foundry

## Requirements
* PCF with container networking enabled (1.12 or later recommended)
* Spring cloud services with service registry
* Maven intalled (e.g. `brew install maven`)

## Information
This is a pair of apps, a backend and frontend that use Eureka's [direct registration method](http://docs.pivotal.io/spring-cloud-services/1-4/common/service-registry/writing-backend-applications.html#register-using-c2c) to enable container-to-container networking.

The frontend simply prints out whatever the backend sends it, or an error if it cannot connect (e.g. the network is blocked).

## Building
Run the provided script `build.sh`.

## Set up
Create a c2c service registry:
```
$ cf create-service p-service-registry standard c2c-registry
```

This may take some time to come up. On PWS I had to wait about 2-3 minutes.

Next push the backend and frontend apps:
```
$ pushd frontend && cf push && popd && pushd backend && cf push && popd
```

Verify the backend app is working as intended by visiting it, for example:
```
$ curl http://c2c-backend-random.cfapps.io
```

It should print a bit of information about itself:
```
Remote hello from c2c-backend (IP: 10.240.155.75:8080)
```

Now check the frontend:
```
$ curl http://c2c-frontend-random.cfapps.io
```

It should return an error like this:
```
Could not reach backend (networking problem?)
```

Enable C2C networking between the apps:
```
cf add-network-policy c2c-frontend --destination-app c2c-backend
```

It may take a couple of seconds to work, but repeating the frontend request should now show it's connected to the backend directly:
```
Remote hello from c2c-backend (IP: 10.240.155.75:8080)
```

You can then disable the policy again if you like:
```
cf remove-network-policy c2c-frontend --destination-app c2c-backend --port 8080 --protocol tcp
```

