export interface ShelfDetail {

    deviceDetails: DeviceDetail[],
    shelfPositionDetails: ShelfPositionDetail,
    shelfDetails: ShelfDetails
}

export interface DeviceDetail {
    deviceType: string,
    name: string,
    id: number
}

export interface ShelfPositionDetail {
    name: string,
    id: number
}

export interface ShelfDetails {
    name: string,
    shelfType: string,
    id: number
}