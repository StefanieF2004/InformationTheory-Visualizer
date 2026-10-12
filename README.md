# Interactive Educational Software for Source Coding Algorithm Visualization

An interactive, desktop-based educational Java application designed to visualize, step through, and evaluate fundamental source coding algorithms in Information Theory and Coding.

---

##  Table of Contents
1. [Overview](#-overview)
2. [Key Features](#-key-features)
3. [Theoretical Background & Mathematical Formulations](#-theoretical-background--mathematical-formulations)
4. [Supported Coding Algorithms](#-supported-coding-algorithms)
5. [Software Architecture & Core Classes](#-software-architecture--core-classes)
6. [User Interface & Visual Demonstrations](#-user-interface--visual-demonstrations)
7. [Installation & Execution](#-installation--execution)

---

##  Overview

In modern information systems, data compression and source coding are pivotal for optimizing bandwidth and storage. However, grasping abstract mathematical compression models through static textbook descriptions often presents a steep learning curve for students.

This project addresses this educational gap by offering an interactive educational visualizer. It bridges mathematical theory and practical execution by providing real-time, step-by-step graphical tree construction, step-by-step arithmetic table updates, dynamic mouse controls (zoom/pan), and immediate metric calculations (Entropy, Average Length, and Efficiency).

---

##  Key Features

- **Interactive Step-by-Step Navigation:** Full step-by-step execution control using **Next Step** and **Previous Step** to analyze every tree split, node merge, and codeword assignment.
- **4 Core Algorithms Implemented:**
  - **Shannon-Fano Coding** (Recursive top-down binary splitting)
  - **Huffman Coding** (Optimal bottom-up binary & multi-base tree construction)
  - **Shannon Binary Coding** (Arithmetic conversion using cumulative probabilities)
  - **Gilbert-Moore Coding** (Alphabetical order-preserving coding)
- **Dynamic Graphical Canvas (`Graphics2D`):**
  - High-resolution rendering with Anti-Aliasing enabled.
  - Interactive viewport handling: Smooth Mouse Dragging (Panning) and Mouse Wheel Zooming ($20\%$ to $400\%$).
- **Flexible Input Methods:** Support for manual probability input, uniform distribution generation, and random probability synthesis.
- **Automatic Theoretical Evaluation:** Instant calculation of source entropy, average code length, and overall coding efficiency.

---

##  Theoretical Background & Mathematical Formulations

The software automatically evaluates generated codebooks against fundamental Information Theory bounds:

### 1. Entropy $H(X)$
Represents the average amount of information produced by a discrete memoryless source $X = \{x_1, x_2, \dots, x_N\}$ with probability distribution $P = \{p_1, p_2, \dots, p_N\}$:
$$H(X) = -\sum_{i=1}^{N} p_i \log_b(p_i)$$
*(Where $b$ represents the base of the code alphabet, e.g., $b = 2$ for binary codes).*

### 2. Average Codeword Length $L$
The expected length of codewords per source symbol:
$$L = \sum_{i=1}^{N} p_i \cdot n_i$$
where $n_i$ denotes the length (in characters/bits) of the assigned codeword for symbol $x_i$.

### 3. Efficiency $\eta$ and Redundancy $R$
The efficiency of the generated code reflects how close the average codeword length comes to the theoretical lower limit defined by source entropy:
$$\eta = \left( \frac{H(X)}{L \cdot \log_2(b)} \right) \times 100\%$$
Code redundancy $R$ is given by:
$$R = 100\% - \eta$$

---

##  Supported Coding Algorithms

### 1. Shannon-Fano Coding
- **Approach:** Top-Down recursive partitioning.
- **Procedure:** Sorts probabilities in descending order, then repeatedly divides the symbol set into two subgroups such that the sum of probabilities in both groups is as equal as possible. Assigns `0` to the upper group and `1` to the lower group.

### 2. Huffman Coding
- **Approach:** Bottom-Up tree construction.
- **Procedure:** Repeatedly combines the two smallest probability nodes into a new composite parent node until a single root is formed.
- **Multi-base Support:** Automatically appends dummy nodes with $p_i = 0$ when building $b$-ary trees ($b > 2$) to satisfy $N \equiv 1 \pmod{b-1}$.

### 3. Shannon Binary Coding
- **Approach:** Cumulative distribution arithmetic.
- **Procedure:**
  1. Calculates individual codeword lengths: $n_i = \lceil -\log_2(p_i) \rceil$.
  2. Computes cumulative probability sums: $q_1 = 0, \quad q_i = \sum_{j=1}^{i-1} p_j$.
  3. Converts $q_i$ to binary fractional representation up to $n_i$ decimal places.

### 4. Gilbert-Moore Coding
- **Approach:** Alphabetical order-preserving coding.
- **Procedure:** Ensures lexicographical symbol order remains intact by utilizing modified mid-point cumulative probabilities:
  $$q_i = \sum_{j=1}^{i-1} p_j + \frac{p_i}{2}$$
  Codeword length is determined by $n_i = \lceil -\log_2(p_i) \rceil + 1$.

---

##  Software Architecture & Core Classes

The application follows an Object-Oriented design optimized for snapshot state management during dynamic step execution:

| Class Name | Primary Responsibility |
| :--- | :--- |
| **`Symbol`** | Encapsulates source symbol data: label, original index, assigned code, probability $p_i$, and $q_i$ values. |
| **`Node`** | Represents tree nodes/leaves. Features a recursive `makeCopy()` deep-copy method to freeze tree states at each step for step-back functionality. |
| **`VisualLine`** | Stores geometric coordinates $(x_1, y_1) \to (x_2, y_2)$ and edge labels (`0`/`1`) for dynamic tree drawing. |
| **`GraphicsPanel`** | Custom `JPanel` responsible for rendering trees and tables using Java `Graphics2D` transformation matrices (pan/zoom). |
| **`App_za_KODOVI`** | Main application frame holding GUI components, event listeners, input handlers, and step control loops. |

---

##  User Interface & Visual Demonstrations

Below are key screenshots showcasing all 4 algorithms supported by the application:

### 1. Shannon-Fano Code Visualization
Recursive top-down splitting and tree generation.  
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/09811f98-9819-43d9-a85a-956134280b63" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/cc103f4c-3a0a-44c0-a8cd-0767ab855250" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/5d871438-ca84-47aa-b4cf-1ab21ac51163" />
...
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/5ce2f9e2-2f36-49a4-9c9e-57026f8231eb" />


### 2. Huffman Algorithm Visualization
Bottom-up tree construction with optimal merging.  
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/930fab40-1f39-4378-9921-0f61efa122d1" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/cc94cdd8-d69b-4f22-b844-3225e60adf89" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/fb535de9-08c4-48fd-afe7-25316782afe6" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/4f200b37-4e28-4215-99d5-0fc4b78e730f" />
...
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/63679027-3c29-48e6-8fed-c59b109d6bb2" />


### 3. Shannon Binary Code Visualization
Cumulative probability arithmetic and fractional binary conversion.  
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/b4b86c78-552f-447c-a622-98592d69aba4" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/c52eb9b2-3fcf-4ea1-9e62-5d4af433dac1" />
...
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/b33384b9-d574-421b-bf6d-db7d6ca15aaa" />


### 4. Gilbert-Moore Code Visualization
Alphabetical order-preserving arithmetic coding.  
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/5764b817-f9d3-4f52-b231-be8b53cdaebb" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/2ad21e21-9dea-457f-8552-b8c7b9ffca04" />
...
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/a022dc9e-dc42-416f-b492-c489a1bb2ee3" />


---

##  Installation & Execution

### Prerequisites
- Java Development Kit (**JDK 8** or newer).

### Running via Command Line
1. Clone the repository:
   ```bash
   git clone [https://github.com/StefanieF2004/InformationTheory-Visualizer.git](https://github.com/StefanieF2004/InformationTheory-Visualizer.git)
