# Sofia plugin

Betterfactory persistence is an API that works as a notification service implementing
NGSIv2 along with [Orion Context Broker](https://github.com/telefonicaid/fiware-orion).

## Table of Contents

- [Project Description](#project-description)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [License](#license)

### Project Description

This project uses the subscribe feature from Orion Context Broker. Subscribing means that changes in attributes from
entities(ex. temperature from temperature sensor) are captured and displayed to the user. User is being notified if any
of the attributes change. This way, Betterfactory persistence API informs the user what changes were made in real time
so that adequate measures could be taken.

### Installation

Firstly, make sure you
have [Apache Maven](https://maven.apache.org/install.html), [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
and [Docker](https://docs.docker.com/engine/install/) on your machine.

Clone the repository as in your target directory:

```console
git clone https://github.com/european-dynamics-rnd/betterfactory-persistence.git
```

Enter the repository you just cloned:

```console
cd betterfactory-persistence  
```

Then package so it can be run in docker:

```console
mvn package
```

Change directory in order to run dockerfile:

```console
cd docker
```

Simply run the dockerfile:

```console
docker-compose up
```

You should see a message from docker that containers have been created and the application should start by default on
port 8080.

After everything has been set up you're ready to go.

### Usage

Our application comes fresh out of the box with the temperature entity to test it out as it will be shown in the
following examples.

Please note that creation of the entity will be shown as a simulation of IoT device creating one
via [Postman](https://www.postman.com/downloads/). It could be done using curl but we recommend using postman.

If this is your first run, there should be no entities created, you can check here:

```console
http://localhost:1026/v2/entities
```

Subscribtions can be checked here:

```console
http://localhost:1026/v2/subscriptions
```

In order to create one, open postman and write the following:

![postman image](https://gcdnb.pbrd.co/images/z72T0FkIOdYi.png?o=1)

We are creating an entity on the Orion Context Broker, just like IoT device would with its own type, id and attributes.
In this case we are creating a temperature sensor with temperature attribute.

Next, we are to subscribe to this entity to recieve notifications if the value of the temperature changes from the
temperature sensor.

In order to subscribe, write a POST method in postman like following:

![postman image2](https://gcdnb.pbrd.co/images/ISN2bTcsmD4Z.png?o=1)

Notice the "url" in the subscription JSON raw body, that's the url which we have a post method written in our code in
order to receive notifications to this particular sensor. According to Orion Context Broker only one url is used for an
entity.

Created enitity and subscription can be checked as existing on the links that we used in the begining of this chapter.
Now, lets simulate the change of the temperature value in this sensor that we subscribed for using PUT method:

![postman image3](https://gcdnb.pbrd.co/images/IujTZwM2cixQ.png?o=1)

If you've opened docker in the terminal as we did in this example you should see the message that the temperature value
has changed:

![postman image4](https://gcdnb.pbrd.co/images/DoaERyRfmCh3.png?o=1)

Voilà, you got yourself a notification for the entity that you've subscribed for.

Last thing, deletion of entites and subscriptions for our examples can be done by id. Just write a DELETE method in
postman with these url's:

```console
http://localhost:1026/v2/entities/TemperatureSensor1
```

```console
http://localhost:1026/v2/subscriptions/647f06b24359227ed108172b
```

### Configuration

Adding more sensors means writing more classes in the code and writing post methods in the controller so that
notifications can be captured and displayed to the user. For example adding a new pressure sensor means making
corresponding class DTO with the adequate attributes as well as writing a POST method in the controller class with
desired url. (ex. for temperature is localhost:8080/temperaturev2).

### License

Orion Context Broker is licensed under [Affero General Public License (GPL)
version 3](./LICENSE).

© 2023 Telefonica Investigación y Desarrollo, S.A.U

There are no problems in using a product licensed under AGPL 3.0. Issues with GPL
(or AGPL) licenses are mostly related with the fact that different people assign different
interpretations on the meaning of the term “derivate work” used in these licenses. Due to this,
some people believe that there is a risk in just _using_ software under GPL or AGPL licenses
(even without _modifying_ it).

For the avoidance of doubt, the owners of this software licensed under an AGPL-3.0 license
wish to make a clarifying public statement as follows:

> Please note that software derived as a result of modifying the source code of this
> software in order to fix a bug or incorporate enhancements is considered a derivative
> work of the product. Software that merely uses or aggregates (i.e. links to) an otherwise
> unmodified version of existing software is not considered a derivative work, and therefore
> it does not need to be released as under the same license, or even released as open source.
