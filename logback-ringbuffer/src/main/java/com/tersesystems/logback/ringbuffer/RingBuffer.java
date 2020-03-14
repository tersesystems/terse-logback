package com.tersesystems.logback.ringbuffer;

import java.util.Queue;
import org.jctools.queues.MessagePassingQueue;

// Copied from MessagePassingQueue
public interface RingBuffer<T> {
  int UNBOUNDED_CAPACITY = -1;

  interface Supplier<T> {
    /**
     * This method will return the next value to be written to the queue. As such the queue
     * implementations are commited to insert the value once the call is made.
     *
     * <p>Users should be aware that underlying queue implementations may upfront claim parts of the
     * queue for batch operations and this will effect the view on the queue from the supplier
     * method. In particular size and any offer methods may take the view that the full batch has
     * already happened.
     *
     * <p><b>WARNING</b>: this method is assumed to never throw. Breaking this assumption can lead
     * to a broken queue.
     *
     * <p><b>WARNING</b>: this method is assumed to never return {@code null}. Breaking this
     * assumption can lead to a broken queue.
     *
     * @return new element, NEVER {@code null}
     */
    T get();
  }

  interface Consumer<T> {
    /**
     * This method will process an element already removed from the queue. This method is expected
     * to never throw an exception.
     *
     * <p>Users should be aware that underlying queue implementations may upfront claim parts of the
     * queue for batch operations and this will effect the view on the queue from the accept method.
     * In particular size and any poll/peek methods may take the view that the full batch has
     * already happened.
     *
     * <p><b>WARNING</b>: this method is assumed to never throw. Breaking this assumption can lead
     * to a broken queue.
     *
     * @param e not {@code null}
     */
    void accept(T e);
  }

  interface WaitStrategy {
    /**
     * This method can implement static or dynamic backoff. Dynamic backoff will rely on the counter
     * for estimating how long the caller has been idling. The expected usage is:
     *
     * <p>
     *
     * <pre>
     * <code>
     * int ic = 0;
     * while(true) {
     *   if(!isGodotArrived()) {
     *     ic = w.idle(ic);
     *     continue;
     *   }
     *   ic = 0;
     *   // party with Godot until he goes again
     * }
     * </code>
     * </pre>
     *
     * @param idleCounter idle calls counter, managed by the idle method until reset
     * @return new counter value to be used on subsequent idle cycle
     */
    int idle(int idleCounter);
  }

  interface ExitCondition {

    /**
     * This method should be implemented such that the flag read or determination cannot be hoisted
     * out of a loop which notmally means a volatile load, but with JDK9 VarHandles may mean
     * getOpaque.
     *
     * @return true as long as we should keep running
     */
    boolean keepRunning();
  }

  /**
   * Called from a producer thread subject to the restrictions appropriate to the implementation and
   * according to the {@link Queue#offer(Object)} interface.
   *
   * @param e not {@code null}, will throw NPE if it is
   * @return true if element was inserted into the queue, false iff full
   */
  boolean offer(T e);

  /**
   * Called from the consumer thread subject to the restrictions appropriate to the implementation
   * and according to the {@link Queue#poll()} interface.
   *
   * @return a message from the queue if one is available, {@code null} iff empty
   */
  T poll();

  /**
   * Called from the consumer thread subject to the restrictions appropriate to the implementation
   * and according to the {@link Queue#peek()} interface.
   *
   * @return a message from the queue if one is available, {@code null} iff empty
   */
  T peek();

  /**
   * This method's accuracy is subject to concurrent modifications happening as the size is
   * estimated and as such is a best effort rather than absolute value. For some implementations
   * this method may be O(n) rather than O(1).
   *
   * @return number of messages in the queue, between 0 and {@link Integer#MAX_VALUE} but less or
   *     equals to capacity (if bounded).
   */
  int size();

  /**
   * Removes all items from the queue. Called from the consumer thread subject to the restrictions
   * appropriate to the implementation and according to the {@link Queue#clear()} interface.
   */
  void clear();

  /**
   * This method's accuracy is subject to concurrent modifications happening as the observation is
   * carried out.
   *
   * @return true if empty, false otherwise
   */
  boolean isEmpty();

  /**
   * @return the capacity of this queue or {@link MessagePassingQueue#UNBOUNDED_CAPACITY} if not
   *     bounded
   */
  int capacity();

  /**
   * Called from a producer thread subject to the restrictions appropriate to the implementation. As
   * opposed to {@link Queue#offer(Object)} this method may return false without the queue being
   * full.
   *
   * @param e not {@code null}, will throw NPE if it is
   * @return true if element was inserted into the queue, false if unable to offer
   */
  boolean relaxedOffer(T e);

  /**
   * Called from the consumer thread subject to the restrictions appropriate to the implementation.
   * As opposed to {@link Queue#poll()} this method may return {@code null} without the queue being
   * empty.
   *
   * @return a message from the queue if one is available, {@code null} if unable to poll
   */
  T relaxedPoll();

  /**
   * Called from the consumer thread subject to the restrictions appropriate to the implementation.
   * As opposed to {@link Queue#peek()} this method may return {@code null} without the queue being
   * empty.
   *
   * @return a message from the queue if one is available, {@code null} if unable to peek
   */
  T relaxedPeek();

  /**
   * Remove up to <i>limit</i> elements from the queue and hand to consume. This should be
   * semantically similar to:
   *
   * <p>
   *
   * <pre>{@code
   * M m;
   * int i = 0;
   * for(;i < limit && (m = relaxedPoll()) != null; i++){
   *   c.accept(m);
   * }
   * return i;
   * }</pre>
   *
   * <p>There's no strong commitment to the queue being empty at the end of a drain. Called from a
   * consumer thread subject to the restrictions appropriate to the implementation.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Consumer#accept} make
   * sure you have read and understood these before using this method.
   *
   * @return the number of polled elements
   * @throws IllegalArgumentException c is {@code null}
   * @throws IllegalArgumentException if limit is negative
   */
  int drain(Consumer<T> c, int limit);

  /**
   * Stuff the queue with up to <i>limit</i> elements from the supplier. Semantically similar to:
   *
   * <p>
   *
   * <pre>{@code
   * for(int i=0; i < limit && relaxedOffer(s.get()); i++);
   * }</pre>
   *
   * <p>There's no strong commitment to the queue being full at the end of a fill. Called from a
   * producer thread subject to the restrictions appropriate to the implementation.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Supplier#get} make sure
   * you have read and understood these before using this method.
   *
   * @return the number of offered elements
   * @throws IllegalArgumentException s is {@code null}
   * @throws IllegalArgumentException if limit is negative
   */
  int fill(Supplier<T> s, int limit);

  /**
   * Remove all available item from the queue and hand to consume. This should be semantically
   * similar to:
   *
   * <pre>
   * M m;
   * while((m = relaxedPoll()) != null){
   * c.accept(m);
   * }
   * </pre>
   *
   * There's no strong commitment to the queue being empty at the end of a drain. Called from a
   * consumer thread subject to the restrictions appropriate to the implementation.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Consumer#accept} make
   * sure you have read and understood these before using this method.
   *
   * @return the number of polled elements
   * @throws IllegalArgumentException c is {@code null}
   */
  int drain(Consumer<T> c);

  /**
   * Stuff the queue with elements from the supplier. Semantically similar to:
   *
   * <pre>
   * while(relaxedOffer(s.get());
   * </pre>
   *
   * There's no strong commitment to the queue being full at the end of a fill. Called from a
   * producer thread subject to the restrictions appropriate to the implementation.
   *
   * <p>Unbounded queues will fill up the queue with a fixed amount rather than fill up to oblivion.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Supplier#get} make sure
   * you have read and understood these before using this method.
   *
   * @return the number of offered elements
   * @throws IllegalArgumentException s is {@code null}
   */
  int fill(Supplier<T> s);

  /**
   * Remove elements from the queue and hand to consume forever. Semantically similar to:
   *
   * <p>
   *
   * <pre>
   *  int idleCounter = 0;
   *  while (exit.keepRunning()) {
   *      E e = relaxedPoll();
   *      if(e==null){
   *          idleCounter = wait.idle(idleCounter);
   *          continue;
   *      }
   *      idleCounter = 0;
   *      c.accept(e);
   *  }
   * </pre>
   *
   * <p>Called from a consumer thread subject to the restrictions appropriate to the implementation.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Consumer#accept} make
   * sure you have read and understood these before using this method.
   *
   * @throws IllegalArgumentException c OR wait OR exit are {@code null}
   */
  void drain(Consumer<T> c, WaitStrategy wait, ExitCondition exit);

  /**
   * Stuff the queue with elements from the supplier forever. Semantically similar to:
   *
   * <p>
   *
   * <pre>
   * <code>
   *  int idleCounter = 0;
   *  while (exit.keepRunning()) {
   *      E e = s.get();
   *      while (!relaxedOffer(e)) {
   *          idleCounter = wait.idle(idleCounter);
   *          continue;
   *      }
   *      idleCounter = 0;
   *  }
   * </code>
   * </pre>
   *
   * <p>Called from a producer thread subject to the restrictions appropriate to the implementation.
   * The main difference being that implementors MUST assure room in the queue is available BEFORE
   * calling {@link Supplier#get}.
   *
   * <p><b>WARNING</b>: Explicit assumptions are made with regards to {@link Supplier#get} make sure
   * you have read and understood these before using this method.
   *
   * @throws IllegalArgumentException s OR wait OR exit are {@code null}
   */
  void fill(Supplier<T> s, WaitStrategy wait, ExitCondition exit);
}
