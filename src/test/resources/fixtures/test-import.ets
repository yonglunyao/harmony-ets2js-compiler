import { Permissions, abilityAccessCtrl } from '@kit.AbilityKit';
import { PermissionUtil } from '@pura/harmony-utils';

@Component
export struct TestComponent {
  @State message: string = 'Hello World';

  build() {
    Text(this.message)
  }
}
