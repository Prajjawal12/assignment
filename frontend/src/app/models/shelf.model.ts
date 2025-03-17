export interface Shelf {
    id: number;
    name: string;
    shelfType: string;
    associatedShelfPositions: number;
    positions?: number[]
}