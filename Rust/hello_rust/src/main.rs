use std::io::{self, BufRead, Write};
use rustc_ast::ast;
use rustc_driver::{Compilation, CompilerCalls, RustcOptGroup, driver};
use rustc_interface::{interface, Query};

fn main() {
    println!("Welcome to the Rust REPL!");
    println!("Type 'exit' to quit.");

    loop {
        print!(">>> ");
        io::stdout().flush().unwrap();

        let mut input = String::new();
        let stdin = io::stdin().lock().read_line(&mut input);

        if let Err(e) = stdin {
            eprintln!("Error reading input: {}", e);
            continue;
        }

        let input = input.trim();
        if input == "exit" {
            break;
        }

        match eval(input) {
            Ok(output) => println!("{}", output),
            Err(e) => eprintln!("Error: {}", e),
        }
    }

    println!("Goodbye!");
}

fn eval(input: &str) -> Result<String, Box<dyn std::error::Error>> {
    let krate = parse_crate(input)?;
    let result = compile_crate(&krate)?;
    Ok(format!("{}", result))
}

fn parse_crate(input: &str) -> Result<ast::Crate, Box<dyn std::error::Error>> {
    let sess = interface::Config::create_session(vec![]);
    let mut collector = driver::CompileState::new(sess);

    driver::RunCompiler::new(&mut collector, &mut []).parse_crate_from_source_str(
        "repl_input".to_string(),
        input.to_string(),
        Vec::new(),
    )?;

    Ok(collector.parse_sess.take_dep_graph().take_krate())
}

fn compile_crate(krate: &ast::Crate) -> Result<i64, Box<dyn std::error::Error>> {
    let sess = interface::Config::create_session(vec![]);
    let mut compiler_calls = driver::DefaultCallbacks;

    let result = driver::compile_input(
        &sess,
        &mut compiler_calls,
        RustcOptGroup::default(),
        krate,
    )?;

    match result.compilation {
        Compilation::Success(_) => {
            // 在这里执行编译后的代码
            Ok(42)
        }
        Compilation::Error => Err("Compilation error".into()),
    }
}