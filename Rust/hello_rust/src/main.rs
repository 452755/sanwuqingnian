use rand::{self, Rng};

fn main() {
    let num = rand::thread_rng().gen_range(0..10);

    println!("{num}");
}

