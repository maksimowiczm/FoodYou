/**
 * Copyright (C) Read You
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette

/**
 * This is a svg image converted to compose code using AI. The image is from the "Read You" app and
 * is under GPL-3.0 license. Original source and license can be found here:
 * https://github.com/ReadYouApp/ReadYou
 */
@Composable
internal fun ReadYouImage(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    val primaryTonal = remember(colorScheme.primary) { TonalPalette.from(colorScheme.primary) }
    val secondaryTonal =
        remember(colorScheme.secondary) { TonalPalette.from(colorScheme.secondary) }
    val tertiaryTonal = remember(colorScheme.tertiary) { TonalPalette.from(colorScheme.tertiary) }

    val secondary40 = remember(secondaryTonal) { Color(secondaryTonal.tone(40)) }
    val secondary30 = remember(secondaryTonal) { Color(secondaryTonal.tone(30)) }
    val tertiary90 = remember(tertiaryTonal) { Color(tertiaryTonal.tone(90)) }
    val tertiary80 = remember(tertiaryTonal) { Color(tertiaryTonal.tone(80)) }
    val secondary20 = remember(secondaryTonal) { Color(secondaryTonal.tone(20)) }
    val secondary50 = remember(secondaryTonal) { Color(secondaryTonal.tone(50)) }
    val secondary90 = remember(secondaryTonal) { Color(secondaryTonal.tone(90)) }
    val secondary70 = remember(secondaryTonal) { Color(secondaryTonal.tone(70)) }
    val primary50 = remember(primaryTonal) { Color(primaryTonal.tone(50)) }
    val tertiary40 = remember(tertiaryTonal) { Color(tertiaryTonal.tone(40)) }
    val onSurface = colorScheme.onSurface

    Canvas(modifier.aspectRatio(348f / 266f)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val scaleX = canvasWidth / 348f
        val scaleY = canvasHeight / 266f

        // Helper function to create and scale paths from SVG path data
        fun createScaledPath(pathData: String): Path {
            val path = PathParser().parsePathString(pathData).toPath()
            val scaleMatrix = Matrix().apply { scale(scaleX, scaleY) }
            path.transform(scaleMatrix)
            return path
        }

        // Background light purple circles
        drawPath(
            path =
                createScaledPath(
                    "M233.21 136.618C233.206 129.903 231.709 123.273 228.826 117.208C225.944 111.143 221.748 105.795 216.544 101.552C211.339 97.309 205.256 94.2767 198.735 92.675C192.214 91.0733 185.418 90.9423 178.84 92.2915C172.261 93.6407 166.066 96.4362 160.702 100.476C155.337 104.515 150.939 109.697 147.825 115.647C144.711 121.596 142.959 128.163 142.697 134.873C142.434 141.583 143.668 148.268 146.308 154.442C146.264 154.393 146.218 154.345 146.174 154.295C148.185 158.982 150.972 163.296 154.42 167.055C154.43 167.066 154.44 167.077 154.451 167.089C154.729 167.392 155.009 167.693 155.295 167.989C159.432 172.304 164.385 175.754 169.867 178.139C175.349 180.525 181.249 181.797 187.227 181.883L185.697 264.977H190.424L189.467 210.134L196.304 206.534L195.261 204.553L189.423 207.627L188.974 181.879C200.798 181.608 212.047 176.72 220.314 168.262C228.581 159.803 233.21 148.446 233.21 136.618Z"
                ),
            color = secondary90,
        )

        drawPath(
            path =
                createScaledPath(
                    "M347.873 155.448C347.869 149.718 346.592 144.06 344.132 138.885C341.673 133.71 338.092 129.146 333.651 125.526C329.21 121.905 324.019 119.317 318.455 117.951C312.89 116.584 307.091 116.472 301.478 117.623C295.865 118.775 290.578 121.16 286.001 124.607C281.423 128.054 277.67 132.476 275.013 137.552C272.356 142.629 270.861 148.233 270.637 153.959C270.413 159.684 271.466 165.388 273.718 170.657C273.681 170.615 273.642 170.574 273.604 170.532C275.32 174.531 277.699 178.212 280.64 181.419C280.649 181.429 280.658 181.438 280.667 181.448C280.904 181.707 281.143 181.964 281.387 182.216C284.917 185.898 289.144 188.843 293.822 190.878C298.499 192.913 303.534 193.999 308.635 194.072L307.33 264.977H311.363L310.546 218.179L316.38 215.108L315.49 213.417L310.509 216.04L310.126 194.069C320.216 193.837 329.814 189.667 336.869 182.449C343.923 175.232 347.872 165.54 347.873 155.448Z"
                ),
            color = secondary90,
        )

        drawPath(
            path =
                createScaledPath(
                    "M103.169 118.907C103.165 111.266 101.461 103.721 98.1809 96.8188C94.9007 89.9171 90.1262 83.8314 84.2035 79.0028C78.2808 74.1742 71.3582 70.7235 63.9371 68.9008C56.5161 67.0781 48.7825 66.929 41.2968 68.4644C33.811 69.9997 26.7605 73.181 20.6561 77.7778C14.5517 82.3746 9.54628 88.2717 6.00251 95.0419C2.45874 101.812 0.465369 109.286 0.166812 116.922C-0.131744 124.557 1.272 132.164 4.27633 139.19C4.22595 139.134 4.1739 139.08 4.12376 139.023C6.41216 144.357 9.58455 149.266 13.5072 153.544C13.5189 153.557 13.531 153.569 13.5427 153.582C13.8592 153.927 14.1775 154.27 14.5032 154.606C19.2113 159.517 24.848 163.443 31.0861 166.158C37.3241 168.872 44.0392 170.32 50.8414 170.418L49.1005 264.977H54.4791L53.3905 202.567L61.1708 198.471L59.9839 196.216L53.3408 199.714L52.8297 170.413C66.2855 170.105 79.0864 164.543 88.494 154.917C97.9016 145.291 103.169 132.367 103.169 118.907Z"
                ),
            color = secondary90,
        )

        // Pink circle (top)
        drawPath(
            path =
                createScaledPath(
                    "M296.539 67.3864C315.147 67.3864 330.232 52.3014 330.232 33.6932C330.232 15.085 315.147 0 296.539 0C277.931 0 262.846 15.085 262.846 33.6932C262.846 52.3014 277.931 67.3864 296.539 67.3864Z"
                ),
            color = tertiary90,
        )

        // Main figure - Left foot
        drawPath(
            path =
                createScaledPath(
                    "M120.582 258.466L124.001 259.49L129.578 246.79L124.532 245.279L120.582 258.466Z"
                ),
            color = tertiary80,
        )

        drawPath(
            path =
                createScaledPath(
                    "M120.045 257.089L126.778 259.106L126.778 259.106C127.916 259.447 128.872 260.226 129.436 261.271C129.999 262.317 130.124 263.544 129.784 264.682L129.742 264.821L118.718 261.519L120.045 257.089Z"
                ),
            color = secondary50,
        )

        // Main figure - Right foot
        drawPath(
            path =
                createScaledPath(
                    "M153.074 261.26L156.425 260.031L153.281 246.522L148.335 248.335L153.074 261.26Z"
                ),
            color = tertiary80,
        )

        drawPath(
            path =
                createScaledPath(
                    "M151.818 260.479L158.417 258.06L158.418 258.059C159.533 257.651 160.765 257.702 161.843 258.201C162.921 258.701 163.756 259.608 164.165 260.723L164.215 260.86L153.41 264.821L151.818 260.479Z"
                ),
            color = secondary50,
        )

        // Main figure - Legs
        drawPath(
            path =
                createScaledPath(
                    "M126.315 235.475L120.493 254.689L127.479 256.727L134.175 238.969L126.315 235.475Z"
                ),
            color = secondary70,
        )

        drawPath(
            path =
                createScaledPath(
                    "M144.656 239.842L149.896 257.892L157.174 254.398L151.934 237.513L144.656 239.842Z"
                ),
            color = secondary70,
        )

        // Main figure - Body/Torso
        drawPath(
            path =
                createScaledPath(
                    "M141.928 243.098C137.268 243.076 132.619 242.647 128.033 241.816L127.916 241.792V237.352L125.255 237.056L127.332 229.639C126.364 218.211 127.74 203.739 128.185 199.573C128.287 198.597 128.354 198.048 128.354 198.048L130.689 178.199L134.596 174.593L136.371 175.748L139.664 179.04C143.491 188.459 146.528 197.326 146.547 197.896L159.382 239.026L159.296 239.088C154.839 242.234 148.045 243.098 141.928 243.098Z"
                ),
            color = secondary40,
        )

        // Body shadow
        drawPath(
            path =
                createScaledPath(
                    "M132.345 187.003L131.448 192.605L137.523 195.111L132.345 187.003Z"
                ),
            color = onSurface.copy(alpha = 0.2f),
        )

        // Head base
        drawPath(
            path =
                createScaledPath(
                    "M144.266 174.563H130.219C129.931 174.563 129.654 174.448 129.449 174.244C129.245 174.04 129.13 173.762 129.13 173.474V167.419C129.134 165.27 129.991 163.21 131.512 161.692C133.033 160.174 135.094 159.321 137.243 159.321C139.392 159.321 141.453 160.174 142.974 161.692C144.495 163.21 145.352 165.27 145.356 167.419V173.474C145.356 173.762 145.241 174.04 145.037 174.244C144.832 174.448 144.555 174.563 144.266 174.563Z"
                ),
            color = secondary20,
        )

        // Face
        drawPath(
            path =
                createScaledPath(
                    "M138.794 174.199C142.079 174.199 144.742 171.535 144.742 168.25C144.742 164.965 142.079 162.302 138.794 162.302C135.509 162.302 132.845 164.965 132.845 168.25C132.845 171.535 135.509 174.199 138.794 174.199Z"
                ),
            color = tertiary80,
        )

        // Hair details
        drawPath(
            path =
                createScaledPath(
                    "M147.276 168.024H138.686L138.598 166.791L138.158 168.024H136.835L136.661 165.58L135.788 168.024H133.229V167.903C133.231 166.202 133.907 164.57 135.111 163.367C136.314 162.164 137.945 161.487 139.647 161.485H140.858C142.559 161.487 144.191 162.164 145.394 163.367C146.597 164.57 147.274 166.202 147.276 167.903V168.024Z"
                ),
            color = secondary20,
        )

        drawPath(
            path =
                createScaledPath(
                    "M138.616 175.694C138.551 175.694 138.487 175.688 138.423 175.677L132.134 174.567V164.172H139.057L138.886 164.372C136.501 167.153 138.298 171.663 139.581 174.104C139.676 174.283 139.718 174.485 139.703 174.687C139.687 174.888 139.616 175.082 139.496 175.244C139.395 175.383 139.263 175.497 139.11 175.575C138.957 175.653 138.788 175.694 138.616 175.694Z"
                ),
            color = secondary20,
        )

        // Device/Phone
        drawPath(
            path =
                createScaledPath(
                    "M149.714 200.818H146.266C146.156 200.819 146.049 200.778 145.967 200.703C145.885 200.629 145.834 200.527 145.823 200.417L145.133 193.344H150.847L150.157 200.417C150.146 200.527 150.095 200.629 150.013 200.703C149.931 200.778 149.825 200.819 149.714 200.818Z"
                ),
            color = tertiary90,
        )

        drawPath(
            path =
                createScaledPath(
                    "M150.838 194.233H145.143C145.025 194.233 144.911 194.186 144.828 194.103C144.745 194.02 144.698 193.906 144.698 193.788V192.721C144.698 192.603 144.745 192.49 144.828 192.406C144.911 192.323 145.025 192.276 145.143 192.276H150.838C150.956 192.276 151.069 192.323 151.152 192.406C151.236 192.49 151.282 192.603 151.283 192.721V193.788C151.282 193.906 151.236 194.02 151.152 194.103C151.069 194.186 150.956 194.233 150.838 194.233Z"
                ),
            color = tertiary40,
        )

        // Body shadow/overlay
        drawPath(
            path =
                createScaledPath(
                    "M128.498 197.775C130.082 199.608 132.229 200.863 134.603 201.343C136.977 201.823 139.443 201.5 141.614 200.426L142.909 199.785L128.498 197.775Z"
                ),
            color = onSurface.copy(alpha = 0.2f),
        )

        // Hand
        drawPath(
            path =
                createScaledPath(
                    "M149.197 196.406C148.932 196.108 148.605 195.871 148.238 195.713C147.872 195.556 147.475 195.48 147.076 195.492C146.677 195.504 146.286 195.603 145.929 195.783C145.573 195.963 145.26 196.218 145.013 196.532L139.007 194.846L137.093 198.246L145.608 200.508C146.162 200.888 146.836 201.051 147.503 200.967C148.17 200.883 148.782 200.557 149.225 200.051C149.667 199.546 149.909 198.895 149.904 198.223C149.899 197.551 149.647 196.905 149.197 196.406Z"
                ),
            color = tertiary80,
        )

        // Arm/clothing
        drawPath(
            path =
                createScaledPath(
                    "M134.865 200.179C132.01 200.179 128.146 198.505 123.559 195.262C123.304 195.085 123.088 194.857 122.925 194.592C122.762 194.327 122.656 194.031 122.614 193.723C122.275 191.58 124.366 188.682 124.571 188.404L126.769 182.369C126.794 182.271 127.502 179.659 129.28 178.73C129.655 178.538 130.066 178.43 130.487 178.412C130.907 178.394 131.326 178.467 131.716 178.627C135.102 179.86 132.458 189.383 132.095 190.632L136.582 192.744L139.431 194.561L143.334 194.969L142.274 199.872L136.348 200.006C135.863 200.124 135.365 200.182 134.865 200.179Z"
                ),
            color = secondary50,
        )

        // Ptaku
        drawPath(
            path =
                createScaledPath(
                    "M176.055 32.644L181.069 28.6336C177.174 28.2039 175.573 30.3283 174.918 32.0098C171.875 30.7461 168.562 32.4022 168.562 32.4022L178.595 36.0443C178.088 34.6925 177.207 33.5129 176.055 32.644Z"
                ),
            color = secondary30,
        )

        drawPath(
            path =
                createScaledPath(
                    "M252.311 73.4953L257.325 69.4849C253.429 69.0552 251.829 71.1796 251.174 72.8611C248.131 71.5975 244.818 73.2535 244.818 73.2535L254.85 76.8956C254.344 75.5438 253.463 74.3643 252.311 73.4953Z"
                ),
            color = secondary30,
        )

        drawPath(
            path =
                createScaledPath(
                    "M170.063 108.355L175.077 104.345C171.182 103.915 169.582 106.039 168.927 107.721C165.883 106.457 162.571 108.113 162.571 108.113L172.603 111.755C172.097 110.404 171.216 109.224 170.063 108.355Z"
                ),
            color = secondary30,
        )

        // Large purple circle (left background)
        drawPath(
            path =
                createScaledPath(
                    "M96.2325 141.053C133.527 141.053 163.76 110.82 163.76 73.5253C163.76 36.2309 133.527 5.99774 96.2325 5.99774C58.9381 5.99774 28.705 36.2309 28.705 73.5253C28.705 110.82 58.9381 141.053 96.2325 141.053Z"
                ),
            color = primary50,
        )

        // Large circle opacity overlay
        drawPath(
            path =
                createScaledPath(
                    "M46.3724 28.4222C40.0735 43.1562 39.2191 59.6499 43.9616 74.956C48.7042 90.262 58.7347 103.383 72.2608 111.974C85.7869 120.566 101.927 124.067 117.798 121.854C133.668 119.64 148.234 111.856 158.893 99.8914C155.045 108.893 149.287 116.951 142.016 123.506C134.744 130.061 126.135 134.958 116.784 137.855C107.433 140.753 97.5638 141.583 87.8601 140.288C78.1564 138.992 68.8503 135.603 60.5866 130.354C52.3228 125.105 45.2991 118.123 40.0019 109.89C34.7047 101.657 31.2608 92.371 29.9087 82.6751C28.5566 72.9792 29.3287 63.1052 32.1715 53.7373C35.0143 44.3694 39.8598 35.7315 46.3724 28.4222Z"
                ),
            color = onSurface.copy(alpha = 0.2f),
        )

        // Central line
        drawPath(
            path =
                createScaledPath("M96.418 73.5252H96.6037L99.943 264.977H92.8933L96.418 73.5252Z"),
            color = secondary30,
        )

        // Diagonal line
        drawPath(
            path =
                createScaledPath(
                    "M107.158 174.853L95.9954 180.73L97.5511 183.685L108.714 177.808L107.158 174.853Z"
                ),
            color = secondary30,
        )

        // Ground line
        drawPath(
            path = createScaledPath("M348 264.372H0V265.155H348V264.372Z"),
            color = secondary40,
        )

        // Small figures at bottom
        val smallFigurePositions =
            listOf(
                Triple(
                    "M199.518 263.186C199.518 263.186 199.762 258.081 204.756 258.675L199.518 263.186Z",
                    "M198.107 258.328C199.487 258.328 200.607 257.209 200.607 255.828C200.607 254.448 199.487 253.329 198.107 253.329C196.726 253.329 195.607 254.448 195.607 255.828C195.607 257.209 196.726 258.328 198.107 258.328Z",
                    "M198.406 260.038H197.7V264.977H198.406V260.038Z",
                ),
                Triple(
                    "M26.2892 262.481C26.2892 262.481 26.5329 257.376 31.5274 257.969L26.2892 262.481Z",
                    "M24.878 257.622C26.2586 257.622 27.3777 256.503 27.3777 255.123C27.3777 253.742 26.2586 252.623 24.878 252.623C23.4974 252.623 22.3783 253.742 22.3783 255.123C22.3783 256.503 23.4974 257.622 24.878 257.622Z",
                    "M25.1769 259.332H24.4713V264.271H25.1769V259.332Z",
                ),
                Triple(
                    "M67.215 262.833C67.215 262.833 67.4587 257.728 72.4532 258.322L67.215 262.833Z",
                    "M65.8038 257.975C67.1844 257.975 68.3035 256.856 68.3035 255.476C68.3035 254.095 67.1844 252.976 65.8038 252.976C64.4232 252.976 63.304 254.095 63.304 255.476C63.304 256.856 64.4232 257.975 65.8038 257.975Z",
                    "M66.1026 259.685H65.397V264.624H66.1026V259.685Z",
                ),
            )

        smallFigurePositions.forEach { (shadow, head, body) ->
            drawPath(path = createScaledPath(shadow), color = secondary40)
            drawPath(path = createScaledPath(head), color = tertiary90)
            drawPath(path = createScaledPath(body), color = secondary40)
        }

        // Additional small figure shadows
        val shadowPaths =
            listOf(
                "M267.919 265.039C267.919 265.039 268.163 259.934 273.157 260.527L267.919 265.039Z",
                "M220.995 265.039C220.995 265.039 221.239 259.934 226.234 260.527L220.995 265.039Z",
                "M49.8834 265.039C49.8834 265.039 50.1271 259.934 55.1216 260.527L49.8834 265.039Z",
                "M289.087 265.039C289.087 265.039 289.331 259.934 294.326 260.527L289.087 265.039Z",
                "M279.209 265.039C279.209 265.039 279.452 259.934 284.447 260.527L279.209 265.039Z",
                "M258.831 265.039C258.831 265.039 258.587 259.934 253.593 260.527L258.831 265.039Z",
                "M177.685 265.039C177.685 265.039 177.441 259.934 172.447 260.527L177.685 265.039Z",
                "M110.299 265.039C110.299 265.039 110.055 259.934 105.06 260.527L110.299 265.039Z",
                "M38.6785 265.039C38.6785 265.039 38.4349 259.934 33.4403 260.527L38.6785 265.039Z",
                "M310.341 265.039C310.341 265.039 310.097 259.934 305.103 260.527L310.341 265.039Z",
                "M279.999 265.392C279.999 265.392 279.756 260.287 274.761 260.88L279.999 265.392Z",
            )

        shadowPaths.forEach { pathData ->
            drawPath(path = createScaledPath(pathData), color = secondary40)
        }
    }
}
