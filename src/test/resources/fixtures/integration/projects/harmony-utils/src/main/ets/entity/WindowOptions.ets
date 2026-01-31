/**
 * 窗口参数
 */
export class WindowOptions {
  name: string = ''; //窗口名称
  path: string = ''; //窗口要显示的路由,如:pages/index
  width: number = 0; //窗口的宽度，单位为px。
  height: number = 0; //窗口的高度，单位为px。
  x?: number; //窗口在x轴方向移动到的坐标位置，单位为px。
  y?: number; //窗口在y轴方向移动到的坐标位置，单位为px。
  backgroundColor?: string | ColorMetrics; //窗口背景颜色
}