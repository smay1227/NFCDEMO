<h2>android nfc demo</h2>
<p>我这边只有mifare卡，这个demo也是针对m1卡编写的</p>
<p>当android扫描到NFC标签时，会自动寻找最适合的Activity来处理这个TAG，如果有多个Activity满足条件的话，会让用户来选择到底使用哪一个Activity来处理
但我们的业务逻辑会有好多activity要对卡里的内容进行操作，如果让用户自己选择的话会有点不友好。</p>
<p>我的处理是把我们业务上所有对卡的处理，全都放到一个activity里，setResult来把处理结果以及查询信息返回到调用activity那里。处理NFC的activity做成dialog
样式的，提醒将卡片靠近。</p>
<p>M1卡有16个扇区，64块，每个扇区有4块，第一扇区第1块，只能执行读操作，不能进行写操作。每个扇区都是独立的，每个扇区的第四块是存放各个扇区密码的地方。
我们能进行操作的是第一扇区的第2，3块，以及其他扇区的第1，2，3块。</p>
<p>这个demo主要改的信息为：</p>
<p>
   <ul>
      <li>获取卡片的默认出场信息</li>
      <li>修改第一扇区的默认密码</li>
      <li>第一扇区第2块写入自己定义的卡号</li>
      <li>第一扇区第3块写入卡片类型，金额，日期</li>
      <li>修改金额，充值或者刷卡</li>
   </ul>
</p>
