use rand::Rng;
use std::io;

#[derive(Clone, Copy, PartialEq)]
enum Cell {
    Mine,
    Empty,
    Number(u8),
    Flag,
    Revealed,
}

struct Minesweeper {
    grid: Vec<Vec<Cell>>,
    width: usize,
    height: usize,
    mines: usize,
}

impl Minesweeper {
    fn new(width: usize, height: usize, mines: usize) -> Self {
        let mut grid = vec![vec![Cell::Empty; width]; height];
        let mut rng = rand::thread_rng();

        // Place mines randomly
        for _ in 0..mines {
            loop {
                let x = rng.gen_range(0..width);
                let y = rng.gen_range(0..height);

                if grid[y][x] == Cell::Empty {
                    grid[y][x] = Cell::Mine;
                    break;
                }
            }
        }

        // Calculate numbers
        for y in 0..height {
            for x in 0..width {
                if grid[y][x] == Cell::Mine {
                    continue;
                }

                let mut count = 0;
                for ny in y.saturating_sub(1)..=(y + 1).min(height - 1) {
                    for nx in x.saturating_sub(1)..=(x + 1).min(width - 1) {
                        if grid[ny][nx] == Cell::Mine {
                            count += 1;
                        }
                    }
                }

                if count > 0 {
                    grid[y][x] = Cell::Number(count);
                }
            }
        }

        Minesweeper {
            grid,
            width,
            height,
            mines,
        }
    }

    fn display(&self) {
        for row in &self.grid {
            for cell in row {
                match cell {
                    Cell::Mine => print!("* "),
                    Cell::Empty => print!(". "),
                    Cell::Number(n) => print!("{} ", n),
                    Cell::Flag => print!("F "),
                    Cell::Revealed => print!("R "),
                }
            }
            println!();
        }
    }

    fn reveal(&mut self, x: usize, y: usize) -> bool {
        if self.grid[y][x] == Cell::Mine {
            println!("You hit a mine! Game Over!");
            return false;
        } else {
            self.grid[y][x] = Cell::Revealed;
            true
        }
    }

    fn is_victory(&self) -> bool {
        let mut revealed_cells = 0;
        let mut flagged_mines = 0;

        for row in &self.grid {
            for cell in row {
                match cell {
                    Cell::Revealed => revealed_cells += 1,
                    Cell::Flag if *cell == Cell::Mine => flagged_mines += 1,
                    _ => (),
                }
            }
        }

        revealed_cells + self.mines == self.width * self.height && flagged_mines == self.mines
    }

    fn flag(&mut self, x: usize, y: usize) {
        if self.grid[y][x] == Cell::Empty {
            self.grid[y][x] = Cell::Flag;
        }
    }
}

fn main() {
    let mut game = Minesweeper::new(8, 8, 10);

    loop {
        game.display();
        println!("Enter command (reveal x y or flag x y):");

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();
        let parts: Vec<&str> = input.trim().split_whitespace().collect();

        if parts.len() != 3 {
            println!("Invalid input, please enter command and coordinates.");
            continue;
        }

        let command = parts[0];
        let x: usize = parts[1].parse().unwrap();
        let y: usize = parts[2].parse().unwrap();

        match command {
            "reveal" => {
                if !game.reveal(x, y) {
                    break;
                }
            }
            "flag" => {
                game.flag(x, y);
            }
            _ => println!("Unknown command."),
        }

        if game.is_victory() {
            println!("Congratulations! You've won!");
            break;
        }
    }
}