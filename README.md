# C2C PoC Code
Pair of backend/frontend apps to demonstrate C2C networking in Pivotal Cloud Foundry

![GUI Screenshot](screenshot.png)

## Requirements
* PCF with container networking enabled (1.12 or later recommended)
* Spring cloud services with service registry
* Maven intalled (e.g. `brew install maven`)

## Information
This is a pair of apps, a backend and frontend that use Eureka's [direct registration method](http://docs.pivotal.io/spring-cloud-services/1-4/common/service-registry/writing-backend-applications.html#register-using-c2c) to enable container-to-container networking.

The frontend simply prints out whatever the backend sends it, or an error if it cannot connect (e.g. the network is blocked).

## Building
Run the provided script `build.sh`.

## PCF Requirements
You require the `network.write` permission in Cloud Foundry to make network changes. To do so:

### Install the UAAC client
```
$ gem install cf-uaac
```
### Log in
```
$ uaac target uaa.sys.yourdomain.com
$ uaac token client get admin -s MyAdminPassword
```
You can find your admin password in `Elastic Runtime -> Credentials -> Admin Client Credentials`
### Add a user to the `network.write` group:
```
$ uaac member add network.write myuser
```
Note: `network.write` allows a user to modify networking on spaces where they are a developer, for foundation-wide network access use `network.admin` instead.

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

## Self-signed certificates
If you're using self-signed certificates, Eureka registration won't work by default. To enable it, add your API server to the trust store:
```
$ cf set-env c2c-backend TRUST_CERTS api.apps.mydomain.com
$ cf set-env c2c-frontend TRUST_CERTS api.apps.mydomain.com
```
Now restart the apps:
```
cf restart c2c-backend && cf restart c2c-frontend
```

## Verify everything is working
Verify the backend app is working as intended by visiting it, for example:
```
$ curl http://c2c-backend-random.cfapps.io/ping
```

It should print a bit of information about itself:
```
Remote hello from c2c-backend (IP: 10.240.155.75:8080)
```

Now check the frontend:
```
$ curl http://c2c-frontend-random.cfapps.io/ping
```

It should return an error like this:
```
Could not reach backend (networking problem?)
```

## Enable C2C Networking
Enable C2C networking between the apps:
```
cf add-network-policy c2c-frontend --destination-app c2c-backend
```

It may take a couple of seconds to work, but repeating the frontend request should now show it's connected to the backend directly:
```
Remote hello from c2c-backend (IP: 10.240.155.75:8080)
```

You can be sure it's working properly by removing the route from the backend app:
```
cf unmap-route c2c-backend mydomain --hostname c2c-backend-random
```

You can then disable the policy again if you like:
```
cf remove-network-policy c2c-frontend --destination-app c2c-backend --port 8080 --protocol tcp
```
## Advanced Testing
There is a basic website available on the frontend component that allows a bit more advanced testing.

You can initiate requests from the frontend or backend to arbitrary URLs. This can be used to test CNI integration from 3rd parties.

For example, a lower level CNI policy may forbid the frontend app from accessing an external IP, but allow the backend to do so. This will allow you to test/verify.
