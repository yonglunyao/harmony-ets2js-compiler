import abilityTest from './Ability.test';
import CryptographyPageUITest from './CryptographyPageUITest.test';
import FileSystemPageUITest from './FileSystemPageUITest.test';
import ImagePageUITest from './ImagePageUITest.test';
import MainPageUITest from './MainPageUITest.test';
import NfcPageUITest from './NfcPageUITest.test';
import UtilsTest from './UtilsTest.test';
import VideoPageUITest from './VideoPageUITest.test';

export default function testsuite() {
  abilityTest();
  CryptographyPageUITest();
  FileSystemPageUITest();
  ImagePageUITest();
  MainPageUITest();
  NfcPageUITest();
  UtilsTest();
  VideoPageUITest();
}