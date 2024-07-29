use std::io::{self, stdout};

use std::mem;

use crossterm::{
    event::{self, Event, KeyCode},
    terminal::{disable_raw_mode, enable_raw_mode, EnterAlternateScreen, LeaveAlternateScreen},
    ExecutableCommand,
};
use ratatui::{prelude::*, widgets::*};

fn main() {
    let str_z: &str ="中e";
    let str_e: &str = "e";
    let char_z: char ='中';
    let char_e: char = 'e';
    let str_z_size_3: usize = mem::size_of_val(str_z); //3 中文Str Unicode 1~4,此中为3
    let str_e_size_1: usize = mem::size_of_val(str_e); //1 英文字符，占用1个字节(ASCII编码的"e”)
    let str_z_ref_size_16 :usize= mem::size_of_val(&str_z);//16 引用的大小，而不是它们所指向的字符串内容的大小,指针+长度信息 各8个字节
    let str_e_ref_size_16 :usize = mem::size_of_val(&str_e); // 16 同上
    let char_z_size_4:usize = mem::size_of_val(&char_z); //4 字符(char):表示一个Unicpde码点。char在Rust中占用4个字节，无论字符是英文还是中文
    let char_e_size_4 :usize = mem::size_of_val(&char_e); //4 同上
    
    println!("{str_z_size_3}, {str_e_size_1}, {str_z_ref_size_16}, {str_e_ref_size_16}, {char_z_size_4}, {char_e_size_4}")
}

fn main1() -> io::Result<()> {
    enable_raw_mode()?;
    stdout().execute(EnterAlternateScreen)?;
    let mut terminal = Terminal::new(CrosstermBackend::new(stdout()))?;

    let mut should_quit = false;
    while !should_quit {
        terminal.draw(ui)?;
        should_quit = handle_events()?;
    }

    disable_raw_mode()?;
    stdout().execute(LeaveAlternateScreen)?;
    Ok(())
}

fn handle_events() -> io::Result<bool> {
    if event::poll(std::time::Duration::from_millis(50))? {
        if let Event::Key(key) = event::read()? {
            if key.kind == event::KeyEventKind::Press && key.code == KeyCode::Char('q') {
                return Ok(true);
            }
        }
    }
    Ok(false)
}

fn ui(frame: &mut Frame) {
    let main_layout = Layout::new(
        Direction::Vertical,
        [
            Constraint::Length(1),
            Constraint::Min(0),
            Constraint::Length(1),
        ],
    )
    .split(frame.size());
    frame.render_widget(
        Block::new().borders(Borders::TOP).title("Title Bar"),
        main_layout[0],
    );
    frame.render_widget(
        Block::new().borders(Borders::TOP).title("Status Bar"),
        main_layout[2],
    );

    let inner_layout = Layout::new(
        Direction::Horizontal,
        [Constraint::Percentage(50), Constraint::Percentage(50)],
    )
    .split(main_layout[1]);
    frame.render_widget(
        Block::bordered().title("Left"),
        inner_layout[0],
    );
    frame.render_widget(
        Block::bordered().title("Right"),
        inner_layout[1],
    );

    let areas = Layout::new(
        Direction::Vertical,
        [
            Constraint::Length(1),
            Constraint::Length(1),
            Constraint::Length(1),
            Constraint::Length(1),
            Constraint::Min(0),
        ],
    )
    .split(frame.size());

    let span1 = Span::raw("Hello ");
    let span2 = Span::styled(
        "World",
        Style::new()
            .fg(Color::Green)
            .bg(Color::White)
            .add_modifier(Modifier::BOLD),
    );
    let span3 = "!".red().on_light_yellow().italic();

    let line = Line::from(vec![span1, span2, span3]);
    let text: Text = Text::from(vec![line]);

    frame.render_widget(Paragraph::new(text), areas[0]);
    // or using the short-hand syntax and implicit conversions
    frame.render_widget(
        Paragraph::new("Hello World!".red().on_white().bold()),
        areas[1],
    );

    // to style the whole widget instead of just the text
    frame.render_widget(
        Paragraph::new("Hello World!").style(Style::new().red().on_white()),
        areas[2],
    );
    // or using the short-hand syntax
    frame.render_widget(Paragraph::new("Hello World!").blue().on_yellow(), areas[3]);
}