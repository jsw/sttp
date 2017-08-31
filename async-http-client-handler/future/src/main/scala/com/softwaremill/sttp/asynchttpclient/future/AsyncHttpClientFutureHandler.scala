package com.softwaremill.sttp.asynchttpclient.future

import java.nio.ByteBuffer

import com.softwaremill.sttp.asynchttpclient.AsyncHttpClientHandler
import com.softwaremill.sttp.{FutureMonad, SttpHandler}
import org.asynchttpclient.{
  AsyncHttpClient,
  AsyncHttpClientConfig,
  DefaultAsyncHttpClient
}
import org.reactivestreams.Publisher

import scala.concurrent.{ExecutionContext, Future}

class AsyncHttpClientFutureHandler private (
    asyncHttpClient: AsyncHttpClient,
    closeClient: Boolean)(implicit ec: ExecutionContext)
    extends AsyncHttpClientHandler[Future, Nothing](asyncHttpClient,
                                                    new FutureMonad,
                                                    closeClient) {

  override protected def streamBodyToPublisher(
      s: Nothing): Publisher[ByteBuffer] = s // nothing is everything

  override protected def publisherToStreamBody(
      p: Publisher[ByteBuffer]): Nothing =
    throw new IllegalStateException("This handler does not support streaming")

  override protected def publisherToString(
      p: Publisher[ByteBuffer]): Future[String] =
    throw new IllegalStateException("This handler does not support streaming")
}

object AsyncHttpClientFutureHandler {

  /**
    * @param ec The execution context for running non-network related operations,
    *           e.g. mapping responses. Defaults to the global execution
    *           context.
    */
  def apply()(implicit ec: ExecutionContext = ExecutionContext.Implicits.global)
    : SttpHandler[Future, Nothing] =
    new AsyncHttpClientFutureHandler(new DefaultAsyncHttpClient(),
                                     closeClient = true)

  /**
    * @param ec The execution context for running non-network related operations,
    *           e.g. mapping responses. Defaults to the global execution
    *           context.
    */
  def usingConfig(cfg: AsyncHttpClientConfig)(
      implicit ec: ExecutionContext = ExecutionContext.Implicits.global)
    : SttpHandler[Future, Nothing] =
    new AsyncHttpClientFutureHandler(new DefaultAsyncHttpClient(cfg),
                                     closeClient = true)

  /**
    * @param ec The execution context for running non-network related operations,
    *           e.g. mapping responses. Defaults to the global execution
    *           context.
    */
  def usingClient(client: AsyncHttpClient)(implicit ec: ExecutionContext =
                                             ExecutionContext.Implicits.global)
    : SttpHandler[Future, Nothing] =
    new AsyncHttpClientFutureHandler(client, closeClient = false)
}
