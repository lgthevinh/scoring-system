export class TimeUtils {
  static formatUnixTime(unixTime: number, format: string): string {
    const date = new Date(unixTime * 1000); // Convert seconds to milliseconds
    const options: Intl.DateTimeFormatOptions = {};

    // Simple format parsing (you can expand this as needed)
    if (format.includes('YYYY')) options.year = 'numeric';
    if (format.includes('MM')) options.month = '2-digit';
    if (format.includes('DD')) options.day = '2-digit';
    if (format.includes('HH')) options.hour = '2-digit';
    if (format.includes('mm')) options.minute = '2-digit';
    if (format.includes('ss')) options.second = '2-digit';

    return new Intl.DateTimeFormat('en-US', options).format(date);
  }

  static formatIsoTime(isoTime: string, format: string): string {
    const date = new Date(isoTime);
    const options: Intl.DateTimeFormatOptions = {};

    // Simple format parsing (you can expand this as needed)
    if (format.includes('YYYY')) options.year = 'numeric';
    if (format.includes('MM')) options.month = '2-digit';
    if (format.includes('DD')) options.day = '2-digit';
    if (format.includes('HH')) options.hour = '2-digit';
    if (format.includes('mm')) options.minute = '2-digit';
    if (format.includes('ss')) options.second = '2-digit';

    return new Intl.DateTimeFormat('en-US', options).format(date);
  }
}
