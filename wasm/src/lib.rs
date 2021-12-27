use wasm_bindgen::prelude::*;


static neighbours_coordinates_offsets: [(i32, i32); 8] = [
    (-1, -1),
    (-1, 0),
    (-1, 1),
    (0, -1),
    (0, 1),
    (1, -1),
    (1, 0),
    (1, 1)
];

// fn count_alive_neighbours (board: &[u32], width: u32, coordinates: (u32, u32)) -> u32 {
//     return 5;
// }

fn offset_position (size: u32, pos: u32, offset: i32) -> u32 {
    let result = (pos as i32) + offset;

    if result < 0 {
        return ((size as i32) + result) as u32;
    } else if result > ((size as i32) - 1) {
        return ((size as i32)- result) as u32;
    } else {
        return result as u32;
    }
}


fn evolve (board: &[u8], width: u32, new_board: &mut[u8]) {

    let height = board.len() as u32 / width;

    for (pos, status) in board.iter().enumerate() {
        let row_index = (pos as u32) / width;
        let col_index = (pos as u32) % width;
        let mut number_of_alive_neighbours = 0;

        for neighbour_coord in neighbours_coordinates_offsets {
            let (row_offset, col_offset) = neighbour_coord;
            let neigh_row_index = offset_position(height, row_index, row_offset);
            let neigh_col_index = offset_position(width, col_index, col_offset);
            let neigh_pos = (neigh_row_index * width) + neigh_col_index;
            number_of_alive_neighbours += board[neigh_pos as usize];

            // All cells with 4 neighbours or more will die
            // anyway, so no need to count all the neighbours.
            if number_of_alive_neighbours >= 4 {
                break;
            }
        }

        let next_status = match (status, number_of_alive_neighbours) {
            (1, 2) => 1,
            (1, 3) => 1,
            (0, 3) => 1,
            _ => 0
        };

        new_board[pos] = next_status;

        // println!("Pos {0} {1} {2}, {3}, {4}", pos, status, number_of_alive_neighbours, status, next_status);
    }
}


#[wasm_bindgen]
pub fn main (board: &[u8], width: u32, new_board: &mut [u8]) {
    println!("CXX");
    // let mut new_board: [u32; 12] = [0; 12];
    evolve(board, width, new_board);
}
