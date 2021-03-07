# AStupidlySimpleBlockchain

The Stupidly Simple (SS) Blockchain is a collection of blocks that represent transactions on the stupidly simple blockchain network. The purpose of this project is to explore the realm of block chain technology and is not designed for production use. Derivatives of this project that are more stable and less experimental would probably be a better choice for further consideration as an actual implementation.

## Getting Started

### Prerequisites

In order to run this project, you will need:
```
JDK >= 1.15
Maven LATEST
```

### Building

In order to build this project, clone to your local repository and execute:

```
mvn clean install
```

If there are tests in the future for unit testing various aspects of the project and you wish skip:

```
mvn clean install -DskipTests
```

## Configuration

Configuration is done in the `application.ymal`, with the different properties being:

Property                            | Default     | Description |
------------------------------------| ----------- |-------------|
`spring.profiles.active`            | `production`| Running in different active profiles changes functionality of the application. |
`server.port`                       | `9000`      | Sets the port to run the REST service on your local machine. |
`blockchain.hotblocks`              | `20`        | Number of blocks to keep in memory before dumping to storage medium. |
`blockchain.p2p.node-name`          | `blockchain-core-node` | The name you want your computer to be identified as on the blockchain (leave as is for anonymous). |
`blockchain.p2p.port`               | `9090`      | The port to open the P2P connections on. | 
`blockchain.p2p.active-connections` | `10`        | The maximum number of active P2P connections to have open at once. |
`blockchain.p2p.timeout`            | `30`        | The timeout before the P2P connection is dropped (in seconds). |

## Running the Tests

Tests can be run during the build proccess: run `mvn clean install` to build with junit tests.

## Authors

* **Nathan Bunch** - *Original Author*

## Contributing

Contributions are welcome!

See also other contributors on the list of [contributors](https://github.com/taranoshi/AStupidlySimpleBlockchain/graphs/contributors).

## License

This project is licensed under the MIT - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgements

NA.
