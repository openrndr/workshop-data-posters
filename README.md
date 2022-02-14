# workshop-data-posters

## Using this project

In [`src/main/kotlin`](src/main/kotlin) you will find all source code used by this framework. This source code is organized in folders.

 * `archives` Framework code that is used to handle archives. You are not expected to write your own archive code.
 * `examples` Example code that demonstrates how to use OPENRNDR and this project.
 * `skeletons` Skeleton code, these could work as a starting point for your own poster generator.
 * `tools` Image and text tools provided by this framework

## Archives and Skeletons

An archive is a collection of texts and accompanying images. This framework provides a number of ways to work with 
archives.

### Local Archive

Local archive is an archive that lives on your local filesystem. An example archive is provided in the 
`archives/example-poetry` folder. When opening that folder you will see three sub-folders (`001`, `002` and `003`). 
Each sub-folder contains text and image media. You can have multiple images and multiple .txt files.

To try a local archive run the [`LocalArchiveSkeleton`](src/main/kotlin/skeletons/LocalArchiveSkeleton.kt) program.

To create a new archive create a folder under `archives`, for example `archives/my-archive`. Copy the file `src/main/kotlin/skeletons/LocalArchiveSkeleton.kt`
to `src/main/kotlin/work/Skeleton01.kt`. 

In `Skeleton01.kt` change the following code from:
```kotlin
val archive = localArchive("archives/example-poetry").iterator()
```

into: 
```kotlin
val archive = localArchive("archives/my-archive").iterator()
```

### Reddit Archive

A Reddit archive is a dynamic archive for which text and images are retrieved from the Reddit website. 

To try a Reddit based archive run the [`RedditSkeleton.kt`](src/main/kotlin/skeletons/RedditSkeleton.kt) program.

To change the Reddit one can edit the `"r/pics"` part below: 

```kotlin
val posts = runBlocking {
    reddit.getCommunityNewPosts("r/pics")
}
```

### DuckDuckGo Archive

A DuckDuckGo based archive uses DuckDuckGo's image to retrieve text and images from an image search. 

To try a DuckDuckGo based archive run the `src/main/kotlin/skeletons/DuckDuckGoSkeleton.kt` program.

The search query can be changed here:
```kotlin
val archive = duckDuckGoSequence("Goofy Pluto").iterator()
```

## Examples

In `src/main/kotlin/examples` you will find two folders; `openrndr` and `framework`. The `openrndr` folder contains 
small examples that demonstrate OPENRNDR features. The `framework` folder contains examples that demonstrates examples
inside a program that is derived from `LocalArchiveSkeleton.kt`. It is recommended to study the `framework` examples as 
they contain valuable hints for building poster generators.

### `framework` examples
  * [Animation001](src/main/kotlin/examples/framework/Animation001.kt) - animating a circle whenever a new article is loaded
  * [Animation002](src/main/kotlin/examples/framework/Animation002.kt) - fading in images and text whenever a new article is loaded
  * [Animation003](src/main/kotlin/examples/framework/Animation003.kt) - animating post filter settings
  * [ImageContext001](src/main/kotlin/examples/framework/ImageContext001.kt) - demonstration of image statistics
  * [ImageTreatment001](src/main/kotlin/examples/framework/ImageTreatment001.kt) - demonstration of image treatments
  * [ImageTreatment002](src/main/kotlin/examples/framework/ImageTreatment002.kt) - demonstration of image treatments
  * [Mask001](src/main/kotlin/examples/framework/Mask001.kt) - Masking an image layer with text
  * [Mask002](src/main/kotlin/examples/framework/Mask002.kt) - Masking an image layer with text, combining blurred and sharp images
  * [Type001](src/main/kotlin/examples/framework/Type001.kt) - Demonstrating type placement
  * [TypeTreatment001](src/main/kotlin/examples/framework/TypeTreatment001.kt) - demonstration of type treatments

  
