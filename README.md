[![Java CI with Maven](https://github.com/stoicswe/yggdrasil-core/actions/workflows/maven.yml/badge.svg)](https://github.com/stoicswe/yggdrasil-core/actions/workflows/maven.yml)
[![CodeQL](https://github.com/stoicswe/yggdrasil-core/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/stoicswe/yggdrasil-core/actions/workflows/codeql-analysis.yml)
# Yggdrasil Core

### What is Yggdrasil?
The Yggdrasil blockchain is a system designed to allow for transfer of value between participants. The blockchain is named after the great tree of Norse mythology that connects the nine worlds: Asgard, Alfheim, Nidavellir, Midgard, Jotunheim, Vanaheim, Niflheim, Muspell, and Hel. Just as the tree is known for the powerful connections it has to the reality described in the works of the Norse, so too does this project strive for strong interlocking bonds between transactions and blocks in the form of the blockcahin. Does this project strive to become great one day? No, but it does seek to explore the wonderful technology that is Blockchains, encryption, and cryptography. This project is meant to learn about the inner workings of cryptocurrency, therefore all implementations are meant to be as simplistic and straightforward as possible.

### Definitions
Here are some definitions for some aspects of the blockchain and cryptocurrency: The ticker symbol of the currency is: `$YGG`, simply...the first few letters of the project's name. The object of value or currency of the project is known as `Yggs` (pronounced "yigs"). The symbol for describing the currency is the "vend", an Old Norse ASCII character. The character is: `Ꝩ` and is added after the numerical value, for example: `0.575Ꝩ`. The reason for choosing this symbol was because it resembled a tree branch visibly. In order to type the Vend character, you can do one of the following:

```
//Windows ALT Code
ALT+42856
//macOS Option Code
Option+A768
//HTML Encoding
&#42856;
```

## Getting Started

### Prerequisites

In order to run this project, you will need:
```
JDK 11 (IBM Semeru)
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

* **[Nathan Bunch](https://github.com/nathanielbunch)** - *Original Author*

## Contributing

Contributions are welcome!

Before contributing, take time to review the [code of conduct](https://github.com/stoicswe/yggdrasil-core/blob/main/docs/CODE_OF_CONDUCT.md) and [contributing](https://github.com/stoicswe/yggdrasil-core/blob/main/docs/CONTRIBUTING.md) documents.

See also other contributors on the list of [contributors](https://github.com/stoicswe/yggdrasil-core/graphs/contributors).

## License

This project is licensed under the MIT - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgements

_Original Author_ [Nathaniel Bunch](https://github.com/stoicswe)
