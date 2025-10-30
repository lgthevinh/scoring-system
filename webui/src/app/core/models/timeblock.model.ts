export interface TimeBlock {
  name: string;
  duration: string;   // minutes as string (matches backend)
  startTime: string;  // ISO string "yyyy-MM-dd'T'HH:mm" without timezone suffix
}
