export class SwitchTest {
    static testSwitch(value: number): string {
        switch (value) {
            case 1:
                return "one";
            case 2:
                return "two";
            default:
                return "other";
        }
    }

    static testSwitchWithString(value: string): number {
        switch (value) {
            case "one":
                return 1;
            case "two":
                return 2;
            default:
                return 0;
        }
    }
}
