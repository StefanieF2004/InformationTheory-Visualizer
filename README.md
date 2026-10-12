# Interactive Educational Software for Source Coding Algorithm Visualization

An interactive, desktop-based educational Java application designed to visualize, step through, and evaluate fundamental source coding algorithms in Information Theory and Coding.

---

## Table of Contents

1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Theoretical Background and Mathematical Formulations](#theoretical-background-and-mathematical-formulations)
4. [Supported Coding Algorithms](#supported-coding-algorithms)
5. [Software Architecture and Core Classes](#software-architecture-and-core-classes)
6. [User Interface and Visual Demonstrations](#user-interface-and-visual-demonstrations)
7. [Installation and Execution](#installation-and-execution)
8. [Technologies Used](#technologies-used)

---

## Overview

In modern information systems, data compression and source coding are essential for optimizing storage and reducing the amount of data required for transmission. However, understanding the mathematical principles behind source coding algorithms can be challenging when they are presented only through theoretical explanations.

This project addresses this challenge by providing an interactive educational visualization tool developed in Java using Swing and Java2D.

The application enables users to enter a probability distribution, select a source coding algorithm, and explore its execution step by step. It combines mathematical explanations with graphical visualizations to help users understand how codewords are generated and how different coding algorithms operate.

The application also calculates important information-theoretic measures, including source entropy, average codeword length, and coding efficiency.

---

## Key Features

- **Interactive Step-by-Step Navigation**
  - Navigate through the algorithm execution using the Next Step and Previous Step buttons.
  - Observe intermediate tree construction and node-merging operations.
  - Review mathematical explanations for individual execution steps.

- **Four Source Coding Algorithms**
  - Shannon-Fano Coding.
  - Huffman Coding.
  - Shannon Binary Coding.
  - Gilbert-Moore Coding.

- **Interactive Graphical Visualization**
  - Dynamic tree and node visualization using Java2D.
  - Anti-aliasing for smoother graphical rendering.
  - Mouse dragging to pan across the visualization.
  - Mouse wheel zooming from 20% to 400%.

- **Probability Input and Validation**
  - Enter source symbol probabilities manually.
  - Validate individual probability values.
  - Check whether the sum of the probabilities equals 1, within a small numerical tolerance.

- **Automatic Information-Theoretic Evaluation**
  - Calculate source entropy.
  - Calculate average codeword length.
  - Calculate coding efficiency.
  - Display generated codewords and their lengths in a results table.

- **Educational Mathematical Explanations**
  - Display cumulative probability calculations.
  - Show codeword length calculations.
  - Illustrate binary fractional conversion.
  - Explain probability partitioning and Huffman node merging.

---

## Theoretical Background and Mathematical Formulations

The application evaluates generated codewords using fundamental concepts from Information Theory.

Let a discrete memoryless source be represented by:

$$X = \{x_1, x_2, \ldots, x_N\}$$

with probability distribution:

$$P = \{p_1, p_2, \ldots, p_N\}$$

where:

$$\sum_{i=1}^{N} p_i = 1$$

### 1. Source Entropy $H(X)$

Source entropy measures the average information produced by a source.

For a binary source coding system, entropy is defined as:

$$H(X) = -\sum_{i=1}^{N} p_i \log_2(p_i)$$

More generally, entropy measured in base-$b$ units is:

$$H_b(X) = -\sum_{i=1}^{N} p_i \log_b(p_i)$$

where $b$ is the coding alphabet size.

For zero-probability symbols, the entropy contribution is conventionally defined as zero.

### 2. Average Codeword Length $L$

The average codeword length represents the expected number of code symbols required to encode a source symbol.

For a code:

$$L = \sum_{i=1}^{N} p_i n_i$$

where:
- $p_i$ is the probability of source symbol $x_i$.
- $n_i$ is the length of its assigned codeword.

### 3. Coding Efficiency $\eta$

Coding efficiency evaluates how effectively the code utilizes the available code alphabet of size $b$. As implemented in the application, efficiency is calculated as:

$$\eta = \frac{H_2(X)}{L \log_2(b)} \times 100\%$$

For binary coding where $b = 2$, this simplifies to:

$$\eta = \frac{H_2(X)}{L} \times 100\%$$

### 4. Coding Redundancy $R$

Coding redundancy can be defined as the difference between 100% efficiency and the calculated efficiency:

$$R = 100\% - \eta$$

---

## Supported Coding Algorithms

### 1. Shannon-Fano Coding

**Approach:** Recursive top-down probability partitioning.

The Shannon-Fano algorithm constructs a binary code tree by repeatedly dividing a sorted set of source symbols into two groups.

Procedure:
1. Sort source symbols in descending order of probability.
2. Calculate the total probability of the current group.
3. Find a division that minimizes the absolute difference between the probabilities of the two resulting groups.
4. Assign bit `0` to the left group and bit `1` to the right group.
5. Repeat the process recursively until each group contains a single symbol.

### 2. Huffman Coding

**Approach:** Bottom-up tree construction.

Huffman coding generates a prefix code by repeatedly combining nodes with the smallest probabilities.

Procedure:
1. Create an initial forest containing one leaf node for each source symbol.
2. Sort the nodes in ascending order of probability.
3. Select up to $b$ nodes with the smallest probabilities.
4. Combine them into a parent node whose probability equals the sum of the child probabilities.
5. Repeat until a single root node remains.
6. Traverse the tree to assign codewords to the source symbols.

**Multi-ary support:** The implementation accepts a coding base $b$ and adds zero-probability dummy nodes when necessary to satisfy the tree construction condition:

$$(N - 1) \bmod (b - 1) = 0$$

### 3. Shannon Binary Coding

**Approach:** Cumulative probability calculation and binary fractional conversion.

The Shannon binary coding procedure uses cumulative probabilities to determine codewords.

First, calculate the codeword length:

$$n_i = \lceil -\log_2(p_i) \rceil$$

Next, calculate the cumulative probabilities:

$$q_1 = 0$$

$$q_i = \sum_{j=1}^{i-1} p_j$$

The cumulative probability $q_i$ is then converted into a binary fractional representation through repeated multiplication by 2.

### 4. Gilbert-Moore Coding

**Approach:** Coding based on cumulative midpoint probabilities.

The Gilbert-Moore algorithm uses the midpoint of each symbol's probability interval to construct its codeword.

For each symbol, calculate:

$$q_i = \sum_{j=1}^{i-1} p_j + \frac{p_i}{2}$$

The codeword length is calculated as:

$$n_i = \lceil -\log_2(p_i) \rceil + 1$$

The value $q_i$ is converted into a binary fractional representation using repeated multiplication by 2.

---

## Software Architecture and Core Classes

The application follows an object-oriented design and uses Java Swing for the graphical interface and Java2D for custom rendering.

| Class | Primary Responsibility |
| :--- | :--- |
| `App_za_KODOVI` | Main application frame. Manages GUI components, probability input, algorithm selection, event listeners, step navigation, and result calculations. |
| `Symbol` | Represents a source symbol and stores its name, probability, and generated codeword. |
| `Node` | Represents a tree node or leaf, including its probability, children, graphical coordinates, and leaf status. Implements recursive deep copying through `makeCopy()`. |
| `VisualLine` | Stores the coordinates and label of a graphical edge connecting two tree nodes. |
| `GraphicsPanel` | Custom Swing panel that renders the coding trees and codeword blocks. Supports zooming, panning, and anti-aliased drawing. |

---

## User Interface and Visual Demonstrations

The following screenshots demonstrate the application's graphical interface and the execution of the supported algorithms.

### 1. Shannon-Fano Code Visualization

Recursive top-down partitioning of source symbols into groups with approximately equal total probabilities.

<img width="1366" height="768" alt="Shannon-Fano initialization" src="https://github.com/user-attachments/assets/09811f98-9819-43d9-a85a-956134280b63" />

<img width="1366" height="768" alt="Shannon-Fano tree construction" src="https://github.com/user-attachments/assets/cc103f4c-3a0a-44c0-a8cd-0767ab855250" />

<img width="1366" height="768" alt="Shannon-Fano partitioning steps" src="https://github.com/user-attachments/assets/5d871438-ca84-47aa-b4cf-1ab21ac51163" />

<img width="1366" height="768" alt="Shannon-Fano final visualization" src="https://github.com/user-attachments/assets/5ce2f9e2-2f36-49a4-9c9e-57026f8231eb" />

### 2. Huffman Algorithm Visualization

Bottom-up construction of a Huffman coding tree through repeated merging of the smallest-probability nodes.

<img width="1366" height="768" alt="Huffman initial forest" src="https://github.com/user-attachments/assets/930fab40-1f39-4378-9921-0f61efa122d1" />

<img width="1366" height="768" alt="Huffman node merging" src="https://github.com/user-attachments/assets/cc94cdd8-d69b-4f22-b844-3225e60adf89" />

<img width="1366" height="768" alt="Huffman intermediate tree" src="https://github.com/user-attachments/assets/fb535de9-08c4-48fd-afe7-25316782afe6" />

<img width="1366" height="768" alt="Huffman tree construction" src="https://github.com/user-attachments/assets/4f200b37-4e28-4215-99d5-0fc4b78e730f" />

<img width="1366" height="768" alt="Huffman final visualization" src="https://github.com/user-attachments/assets/63679027-3c29-48e6-8fed-c59b109d6bb2" />

### 3. Shannon Binary Code Visualization

Cumulative probability calculations and binary fractional conversion for individual source symbols.

<img width="1366" height="768" alt="Shannon binary code calculation" src="https://github.com/user-attachments/assets/b4b86c78-552f-447c-a622-98592d69aba4" />

<img width="1366" height="768" alt="Shannon binary code conversion" src="https://github.com/user-attachments/assets/c52eb9b2-3fcf-4ea1-9e62-5d4af433dac1" />

<img width="1366" height="768" alt="Shannon binary code final visualization" src="https://github.com/user-attachments/assets/b33384b9-d574-421b-bf6d-db7d6ca15aaa" />

### 4. Gilbert-Moore Code Visualization

Midpoint cumulative probability calculations and binary codeword generation.

<img width="1366" height="768" alt="Gilbert-Moore code calculation" src="https://github.com/user-attachments/assets/5764b817-f9d3-4f52-b231-be8b53cdaebb" />

<img width="1366" height="768" alt="Gilbert-Moore binary conversion" src="https://github.com/user-attachments/assets/2ad21e21-9dea-457f-8552-b8c7b9ffca04" />

<img width="1366" height="768" alt="Gilbert-Moore final visualization" src="https://github.com/user-attachments/assets/a022dc9e-dc42-416f-b492-c489a1bb2ee3" />

---

## Installation and Execution

### Prerequisites

- Java Development Kit (JDK 8 or newer).
- Git, if you want to clone the repository.
- An operating system that supports Java Swing.

### 1. Clone the Repository

```bash
git clone [https://github.com/StefanieF2004/InformationTheory-Visualizer.git](https://github.com/StefanieF2004/InformationTheory-Visualizer.git)

```bash
cd InformationTheory-Visualizer
``

### 2. Compile the Application

```bash
javac App_za_KODOVI.java
```

### 3. Run the Application

```bash
java App_za_KODOVI
```
---

## Technologies Used

- **Java** — Core programming language.
- **Java Swing** — Desktop graphical user interface.
- **Java AWT** — Graphics, layout management, fonts, colors, and mouse events.
- **Java2D (`Graphics2D`)** — Custom tree rendering, anti-aliasing, zooming, and panning.
- **Object-Oriented Programming** — Encapsulation, nested classes, recursion, and state management.
- **Information Theory** — Source entropy, prefix coding, probability distributions, and average codeword length.

---

## Copyright and Attribution

Copyright © 2026 StefanieF2004. All rights reserved.

**Author:** StefanieF2004  
**Project:** InformationTheory-Visualizer  
**Repository:** https://github.com/StefanieF2004/InformationTheory-Visualizer
