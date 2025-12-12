# Dungeon Treasure Hunter
*A competitive treasure hunting game featuring advanced pathfinding algorithms, strategic decision-making, and real-time leaderboard tracking.*

---

## Course Information
**Course Code:** ES234317  
**Course Name:** Algorithm and Data Structure  
**Course Convenor:** Renny Pradina Kusumawardhani  
**Academic Year:** 2024/2025  

---

## Group Information
**Group Number:** Group 3  

- Bagas Budisatrio - 5026231045  
- Adesya Naila Salsabila - 5026241004  
- Aliya Nur Kamila Sillia - 5026241008  

---

## Background
In the mystical land of Algorithmia, an ancient dungeon has remained unexplored for centuries. This dungeon contains 25 interconnected chambers filled with valuable artifacts, from common gold coins to legendary dragon scales. The dungeon is a complex network guarded by three powerful bosses, with dangerous passages that deal damage and mystical checkpoint rooms that restore health.

The Adventurers' Guild has opened the dungeon for competitive exploration, where brave treasure hunters compete for fame and fortune. Players must navigate through treacherous paths, survive deadly encounters, collect maximum gold, and claim a spot on the prestigious Leaderboard of Champions.

---

## Game Overview

### Dungeon Features
- 25 interconnected rooms with various danger levels  
- 3 Boss Rooms guarding treasure vaults  
- 3 Boss Treasure Vaults containing high-value Epic/Legendary items  
- 5 Regular Treasure Rooms with common treasures  
- 1 Hidden Vault requiring a Gold Key with legendary treasures  
- 4 Checkpoint Rooms that restore health  
- 9 Monster Rooms that deal damage when traversing  

### Health System
- Start with 100 HP  
- Passages cost or restore health (negative = damage, positive = healing)  
- Death occurs if HP reaches 0 or below  

### Gold System
- Automatic collection when entering treasure rooms  
- Boss Vaults: 3–5 items (2,000–5,000 gold each)  
- Regular Treasure: 2–3 items (200–800 gold each)  
- Hidden Vault: 5–8 legendary items (3,000–6,000 gold each)  
- Compete for Top 5 on the Leaderboard  

---

## Game Modes

### 1. Most Gold Race
Collect maximum gold while traveling from origin to destination through step-by-step movement with real-time strategic decisions.

### 2. Impossible Route Challenge
Find a survivable path where the shortest route (BFS) leads to death, demonstrating that the shortest is not always the safest.

---

## Search Feature
Real-time search functionality to find:
- Nearest checkpoint/healing room  
- Nearest treasure room  
- Nearest boss room  
- Room containing the Gold Key  

Results are provided in two formats:  
- BFS-based: closest by distance (fewest rooms)  
- Bellman-Ford-based: safest path (minimum HP cost)  

---

## Algorithms Used

### 1. Breadth-First Search (BFS)
**Purpose:** Find the shortest path by room count  
**Implementation:**
- Uses a Queue for level-order traversal  
- Tracks visited nodes with a Boolean array  
- Stores the parent array to reconstruct the path  
- Returns the path with the fewest rooms  

**Use Cases:**
- Impossible Challenge comparison  
- Search feature (closest distance)  
- Quick pathfinding when HP is not a concern  

**Time Complexity:** O(V + E)  
**Limitation:** Ignores HP costs and healing, which may lead to deadly paths  

---

### 2. Bellman-Ford Algorithm
**Purpose:** Find the optimal path minimizing HP damage  
**Implementation:**
- Initializes distances to infinity except the source  
- Performs V-1 relaxation iterations (24 iterations for 25 rooms)  
- Handles negative weights (healing checkpoints)  
- Tracks the parent array for path reconstruction  

**Use Cases:**
- Path optimization for survival  
- Search feature (safest path)  
- Impossible Challenge comparison  

**Time Complexity:** O(V × E)  
**Advantage:** Considers both damage and healing, guarantees optimality  

---

### 3. Merge Sort
**Purpose:** Sort leaderboard entries and treasure items  
**Implementation:**
- Divide the array recursively into halves  
- Merge sorted halves in descending order (highest gold first)  
- Stable sorting algorithm  

**Use Cases:**
- Sorting the Top 5 leaderboard by total gold  
- Displaying treasure items by value  

**Time Complexity:** O(n log n)  

---

### 4. HashMap Lookup
**Purpose:** Fast room access and name resolution  
**Implementation:**
- HashMap<Integer, Room> for ID-based lookup  
- HashMap<String, Integer> for name-based lookup (case-insensitive)  

**Use Cases:**
- Instant room access by ID or name  
- Validation of user input  

**Time Complexity:** O(1) average case  

---

### 5. Linear Search
**Purpose:** Check player ranking and key room location  
**Implementation:**
- Simple iteration through leaderboard entries  
- Search for the Gold Key in the predetermined room list  

**Use Cases:**
- Checking if the player made the Top 5  
- BFS variant to find the nearest room by type  

**Time Complexity:** O(n), but with n=5 for leaderboard, effectively O(1)  

---

## Presentation Files
- Initial Presentation - [1_Initial Presentation_Case Study ASD](https://drive.google.com/file/d/1kNyflSq9lOTvN6cc19Bs_PMPO4tV3sON/view?usp=drive_link)  
- Progress Presentation - [2_Progress Presentation_Case Study ASD](https://drive.google.com/file/d/1wgd9qgj7vrtgQZYbBZZ83yMo0t7BOMAt/view?usp=drive_link)  
- Final Presentation - [3_Final Presentation_Case Study ASD](https://drive.google.com/file/d/1GalFkBIh8xsQ_5ChRpVeeHluDSMytoB6/view?usp=drive_link)  

---

## Related Repositories

### Other Groups' Projects
- Group 1: github.com/yelllsit-glitch/Final-Project-ASD  
- Group 2: github.com/riozakyrizky/Final-Project-ASD-2025  
- Group 3: github.com/strupwa/RevASD/  
- Group 4: github.com/Stephanie020207/Connect4AIGame  
- Group 5: github.com/Daffa-001/ASD-IUP-FinalProject  
