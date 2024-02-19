# Nine Men Morris Game

This repository contains a Nine Men Morris game developed for a university assignment, implemented using Java and JavaFX.

## Features
- Two-player gameplay and One player vs basic Bot
- Graphical user interface with board visualization
- Placing and moving pieces
- Forming mills and capturing opponent's pieces
- Win conditions: reducing opponent to two pieces.

## Installation
1. Clone the repository:
    ```git clone https://github.com/Priyesh2202/NineMenMorris.git```

2. Set up JavaFX:
    - Follow the instructions at [JavaFX Setup Guide](https://javabook.bloomu.edu/setupjavafx.html) to configure your IDE (e.g., IntelliJ IDEA) for JavaFX development.

3. Run the application:
    - Open the project in your IDE.
    - Edit the run configuration to include the following VM options:
        ```
        --module-path
        ""YOUR_PATH"\NineMenMorris\openjfx-17.0.7_windows-x64_bin-sdk\javafx-sdk-17.0.7\lib"
        --add-modules=javafx.base,javafx.controls,javafx.graphics,javafx.media,javafx.fxml
        ```
    - Run the main class of the project (main.java.Game).

## Building the JAR
- Ensure Maven is installed and configured.
- Open a terminal in the project directory.
- Run the following commands:
    ```mvn clean install```
  This will create a JAR file named `NineMenMorris-1.0-SNAPSHOT-shaded.jar` in the `target` directory.

- Run the JAR (double click or):
    ```java -jar NineMenMorris-1.0-SNAPSHOT-shaded.jar```

  ## Authors
- Priyesh
- Zhan Zhynn
- Rachel Ng
