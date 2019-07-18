## Java并发程序设计教程

### 实现一个简单的阻塞队列(2) BlockingQ的实现是错误的

#### [API](https://docs.oracle.com/javase/7/docs/api/ )理论依据：

1. public final void wait(long timeout) throws InterruptedException方法的API文档中：
   this method causes the current thread (call it T) to place itself in the wait set for this object and then to relinquish any and all synchronization claims on this object.
1. public final void wait() throws InterruptedException方法的API文档中：
   The current thread must own this object's monitor. The thread releases ownership of this monitor and waits until...

根据以上2点，`offer(Object)`方法当满足 `linkedList.size() == maxLength` 条件时调用 `notFull.wait() `导致当前线程只释放 **notFull的同步锁，并不释放notEmpty的同步锁**。**其他任何线程都无法再次获得 notEmpty的同步锁。**

#### 实际测试：

1. 我们假设maxLength=1以方便测试，然后分别在关键位置打印日志以分析。单元测试项目放在了github上。
1. 启动3（A, B, C)个线程调用offer(Object）
1. 等待1s启动线程D调用take()

##### 测试结果：

```
A: inside put.
B: inside put.
C: inside put.
A: notEmpty acquired.
A: notFull acquired.
A: notFull released.
A: notEmpty released.
C: notEmpty acquired.
C: notFull acquired.
C: notFull wait start.
D: inside take.
```

##### 结果分析：

- A线程: inside put -> notEmpty acquired -> notFull acquired -> notFull released -> notEmpty released
- C 线程: inside put -> notEmpty acquired -> notFull acquired -> notFull wait start
- B线程: inside put
- D线程: inside take
- A线程完成了整个流程，把数据放入了queue中；
- C线程获取了 notEmpty和notFull 2把锁，然后notFull wait start 释放C线程对notFull的锁，但是并未释放notEmpty这个锁
- B线程 和 D线程 再也无法获得 notEmpty锁


#### 测试结果

**测试结果和理论依据一致**