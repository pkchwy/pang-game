# Pang Game 🎯

A classic Pang-style arcade game implemented in Java with modern features including user authentication, multiple difficulty levels, and various game mechanics.

## 📖 About

Pang Game is a recreation of the classic arcade game where players shoot balloons that bounce around the screen. The goal is to pop all balloons while avoiding getting hit by them. This implementation features modern enhancements like user accounts, different arrow types, collectible items, and progressive difficulty levels.

## ✨ Features

- **Classic Pang Gameplay**: Shoot balloons that split into smaller pieces when hit
- **User Authentication System**: Create accounts and track high scores
- **Multiple Difficulty Levels**: Novice, Intermediate, and Advanced modes
- **Special Weapons**: Normal and special arrow types with different effects
- **Collectible Items**: Various power-ups and score boosters scattered throughout levels
- **Sound Effects**: Immersive audio feedback for all game actions
- **Time-based Challenges**: Complete levels within time limits
- **Score System**: Track your progress and compete for high scores
- **Smooth Animations**: Fluid character movements and visual effects

## 🎮 Gameplay

### Controls
- **Arrow Keys**: Move player left/right, climb ladders
- **Spacebar**: Shoot arrows/harpoons
- **Menu Navigation**: Use mouse to navigate menus and options

### Game Mechanics
- Pop balloons by shooting them with your harpoon
- Large balloons split into smaller ones when hit
- Avoid touching balloons or you'll lose a life
- Collect items for points and special abilities
- Clear all balloons to advance to the next level
- Complete levels within the time limit

### Scoring
- Different balloon sizes award different points
- Collecting items provides bonus points
- Time bonuses for quick completion
- Difficulty multipliers affect final scores

## 🛠️ Technical Requirements

- **Java**: JDK 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512MB RAM
- **Graphics**: Basic 2D graphics support

## 🚀 Installation & Running

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd pang-game
   ```

2. **Compile the Java files**:
   ```bash
   javac -cp src src/main/MainClass.java
   ```

3. **Run the game**:
   ```bash
   java -cp src main.MainClass
   ```

   Or if you have compiled class files in a bin directory:
   ```bash
   java -cp bin main.MainClass
   ```

## 📁 Project Structure

```
pang-game/
├── src/
│   ├── auth/           # User authentication system
│   ├── entities/       # Game objects (Player, Balloons, Items, etc.)
│   ├── inputs/         # Input handling
│   ├── levels/         # Level management and design
│   ├── main/           # Main game loop and window management
│   └── utilz/          # Utility classes and helpers
├── res/
│   ├── sound/          # Audio files (.wav)
│   ├── fonts/          # Game fonts
│   └── *.png           # Game sprites and graphics
├── bin/                # Compiled class files (if using IDE)
├── users.txt           # User account storage
└── LICENSE             # MIT License
```

## 🎨 Assets

The game includes a comprehensive set of visual and audio assets:

- **Graphics**: Player animations, balloon sprites, backgrounds, items, and UI elements
- **Audio**: Sound effects for shooting, balloon popping, item collection, and background music
- **Fonts**: Custom fonts for game UI and menus

## 👥 User System

The game features a simple user authentication system:

- Create new user accounts
- Login with existing credentials
- Track individual high scores
- User data stored in `users.txt` file

Default user for testing:
- **Username**: pika
- **Password**: 123

## 🎯 Game Modes

- **Novice**: Easier difficulty with slower balloons
- **Intermediate**: Moderate challenge with standard balloon behavior
- **Advanced**: High difficulty with faster, more unpredictable balloons

## 🔧 Development

### Key Classes

- `Game.java`: Main game loop and state management
- `Player.java`: Player character with movement and shooting mechanics
- `Balloon.java`: Balloon entities with physics and splitting behavior
- `LevelManager.java`: Level progression and management
- `UserManager.java`: User authentication and score tracking

### Adding New Features

1. **New Items**: Extend the `Item.java` class and add to `ItemType` enum
2. **New Levels**: Create level data in the `levels` package
3. **New Balloons**: Modify `Balloon.java` or create new balloon types
4. **Audio**: Add `.wav` files to `res/sound/` directory

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## 🐛 Known Issues

- Ensure all resource files are in the correct `res/` directory structure
- Audio files must be in `.wav` format for proper playback
- User data is stored in plain text (consider encryption for production use)

## 🎉 Credits

- Game developed by Salih Mert Canseven
- Inspired by the classic Pang arcade game
- Built with Java Swing for cross-platform compatibility

---

**Have fun playing Pang Game! 🎈**